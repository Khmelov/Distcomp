package com.lizaveta.notebook.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseMigrationConfig {

    public static final String LIQUIBASE_RUNNER_BEAN = "liquibaseMigrationRunner";

    @Bean(name = LIQUIBASE_RUNNER_BEAN)
    @ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SpringLiquibase liquibaseMigrationRunner(
            final DataSource dataSource,
            @Value("${spring.liquibase.change-log:classpath:db/changelog/db.changelog-master.xml}") final String changeLog,
            @Value("${spring.liquibase.liquibase-schema:}") final String liquibaseSchema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setLiquibaseSchema(liquibaseSchema == null || liquibaseSchema.isBlank() ? null : liquibaseSchema.trim());
        return liquibase;
    }
}
