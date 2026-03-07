package com.distcomp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class LiquibaseCassandraConfig {

    @Value("${cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.keyspace:distcomp}")
    private String keyspace;

    @Value("${cassandra.local-datacenter:datacenter1}")
    private String localDatacenter;

    @Value("${cassandra.username:}")
    private String username;

    @Value("${cassandra.password:}")
    private String password;

    @Bean
    @DependsOn("createKeyspaceIfNotExists")
    public DataSource cassandraDataSource() {
        final String jdbcUrl = String.format(
                "jdbc:cassandra://%s:%d/%s?localdatacenter=%s&compliancemode=Liquibase",
                contactPoints, port, keyspace, localDatacenter
        );

        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("com.ing.data.cassandra.jdbc.CassandraDriver");
        config.setMaximumPoolSize(2);

        if (!username.isEmpty()) {
            config.setUsername(username);
            config.setPassword(password);
        }

        return new HikariDataSource(config);
    }

    @Bean
    public SpringLiquibase liquibase(final DataSource cassandraDataSource) {
        final SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(cassandraDataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        liquibase.setDefaultSchema(keyspace);  // Optional, sets the keyspace
        return liquibase;
    }
}