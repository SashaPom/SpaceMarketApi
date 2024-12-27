package com.cosmo.cats.api.config;

import com.cosmo.cats.api.repository.impl.NaturalIdRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = "com.cosmo.cats.api.repository",
    repositoryBaseClass = NaturalIdRepositoryImpl.class
)
public class JpaRepositoryConfiguration {

}
