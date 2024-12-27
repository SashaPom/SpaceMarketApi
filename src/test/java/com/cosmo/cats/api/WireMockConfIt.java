package com.cosmo.cats.api;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

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
public class WireMockConfIt {

    private static final int PAYMENT_PORT = 8080;
    private static final Path ADVISOR_DOCKERFILE =
            Paths.get("scripts", "docker", "price-advice-mock");
    private static final GenericContainer<?> ADVISOR_SERVICE_CONTAINER =
            new GenericContainer(new ImageFromDockerfile()
                    .withFileFromPath(".", ADVISOR_DOCKERFILE)
                    .withDockerfile(ADVISOR_DOCKERFILE.resolve("Dockerfile")))
                    .withExposedPorts(PAYMENT_PORT);
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .build();

    static {
        ADVISOR_SERVICE_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setupTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("application.price-advisor-service.base-path", wireMockServer::baseUrl);
    }
}
