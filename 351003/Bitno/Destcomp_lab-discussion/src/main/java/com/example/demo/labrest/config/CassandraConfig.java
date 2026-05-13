package com.example.demo.labrest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "com.example.demo.discussion.repository")  // ✅ Укажи свой пакет!
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getContactPoints() {
        return "localhost";
    }

    @Override
    protected int getPort() {
        return 9042;
    }

    @Override
    protected String getKeyspaceName() {
        return "distcomp";
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.NONE;  // ✅ Схема создаётся вручную через CQL
    }
}