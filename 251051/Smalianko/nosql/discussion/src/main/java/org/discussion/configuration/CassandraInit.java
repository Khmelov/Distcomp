package org.discussion.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class CassandraInit {

    private final CqlSession session;

    public CassandraInit(CqlSession session) {
        this.session = session;
    }

    @PostConstruct
    public void init() {
        createKeyspaceAndTables();
        runLiquibase();
    }

    private void createKeyspaceAndTables() {
        session.execute(
                "CREATE KEYSPACE IF NOT EXISTS distcomp " +
                        "WITH replication = {'class':'SimpleStrategy','replication_factor':1};"
        );
    }

    private void runLiquibase() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:cassandra://localhost:9042/distcomp?compliancemode=Liquibase&localdatacenter=datacenter1&schemaagreementtimeout=10000",
                    "cassandra",
                    "cassandra"
            );

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/changelog-master-cassandra.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.forceReleaseLocks();
            liquibase.update("");
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException("Liquibase failed", e);
        }
    }
}