package com.distcomp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.ing.data.cassandra.jdbc.CassandraDriver");
        dataSource.setUrl(jdbcUrl);
        if (!username.isEmpty()) {
            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    @Bean(name = "cassandraLiquibase")
    public SpringLiquibase liquibase(final DataSource cassandraDataSource) {
        final SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(cassandraDataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        liquibase.setDefaultSchema(keyspace);  // Optional, sets the keyspace
        return liquibase;
    }
}