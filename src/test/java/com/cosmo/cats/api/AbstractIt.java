package com.cosmo.cats.api;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class AbstractIt {

    private static final int PAYMENT_PORT = 8080;
    private static final int POSTGRES_PORT = 5432;
    private static final Path ADVISOR_DOCKERFILE =
            Paths.get("scripts", "docker", "price-advice-mock");
    private static final GenericContainer<?> ADVISOR_SERVICE_CONTAINER =
            new GenericContainer(new ImageFromDockerfile()
                    .withFileFromPath(".", ADVISOR_DOCKERFILE)
                    .withDockerfile(ADVISOR_DOCKERFILE.resolve("Dockerfile")))
                    .withExposedPorts(PAYMENT_PORT);

    private static final GenericContainer POSTGRES_CONTAINER = new GenericContainer("postgres:15.6-alpine")
        .withEnv("POSTGRES_PASSWORD", "postgres").withEnv("POSTGRES_DB", "postgres")
        .withExposedPorts(POSTGRES_PORT);
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .build();

    static {
        ADVISOR_SERVICE_CONTAINER.start();
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setupTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("application.price-advisor-service.base-path", wireMockServer::baseUrl);
        registry.add("application.payment-service.base-path", wireMockServer::baseUrl);
        registry.add("spring.datasource.url", () -> format("jdbc:postgresql://%s:%d/postgres",
            POSTGRES_CONTAINER.getHost(), POSTGRES_CONTAINER.getMappedPort(POSTGRES_PORT)));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }
}
