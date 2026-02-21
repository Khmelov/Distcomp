package com.distcomp.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(
        basePackages = "com.distcomp.data.repository",
        repositoryBaseClass = org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository.class
)
public class R2dbcConfig {
}