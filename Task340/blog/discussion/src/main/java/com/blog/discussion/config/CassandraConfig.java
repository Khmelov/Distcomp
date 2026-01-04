package com.blog.discussion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableCassandraRepositories(basePackages = "com.blog.discussion.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name:distcomp}")
    private String keyspaceName;

    @Value("${spring.data.cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${spring.data.cassandra.port:9042}")
    private int port;

    @Override
    protected String getKeyspaceName() {
        System.out.println("=== CASSANDRA CONFIG ===");
        System.out.println("Keyspace: " + keyspaceName);
        System.out.println("Contact points: " + contactPoints);
        System.out.println("Port: " + port);
        return keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.blog.discussion.model"};
    }

    @Override
    public SchemaAction getSchemaAction() {
        System.out.println("Schema action: CREATE_IF_NOT_EXISTS");
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(
                CreateKeyspaceSpecification.createKeyspace(keyspaceName)
                        .ifNotExists()
                        .with(KeyspaceOption.DURABLE_WRITES, true)
                        .withSimpleReplication(1)
        );
    }
}

