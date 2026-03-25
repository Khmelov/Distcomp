package com.lizaveta.discussion.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ensures Liquibase creates keyspace and tables before the CQL session connects to {@code distcomp}.
 */
@Configuration
public class CassandraSessionAfterLiquibaseConfig {

    @Bean
    public static BeanFactoryPostProcessor cassandraSessionDependsOnLiquibase() {
        return beanFactory -> {
            if (beanFactory.containsBeanDefinition("cassandraSession")
                    && beanFactory.containsBeanDefinition("liquibase")) {
                beanFactory.getBeanDefinition("cassandraSession").setDependsOn("liquibase");
            }
        };
    }
}
