package com.task310.blogplatform.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Autowired
    private DataSource dataSource;

    @Bean
    public SpringLiquibase liquibase() {
        logger.info("Initializing Liquibase...");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setDropFirst(false);
        liquibase.setShouldRun(true);
        logger.info("Liquibase configured successfully with changelog: classpath:db/changelog/db.changelog-master.xml");
        return liquibase;
    }
}
