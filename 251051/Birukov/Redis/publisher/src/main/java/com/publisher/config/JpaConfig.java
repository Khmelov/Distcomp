package com.publisher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
	basePackages = "com.publisher.repository",
	entityManagerFactoryRef = "entityManagerFactory"
)
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {}