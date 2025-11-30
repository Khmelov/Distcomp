package org.discussion.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class CassandraPreInitializationConfig {

    private static final String KEYSPACE_NAME = "distcomp";

    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.cassandra.port}")
    private int port;

    @Value("${spring.cassandra.local-datacenter}")
    private String localDatacenter;


    @Bean
    @DependsOn("cassandraSchemaInitializer")
    public Object initializationCheck() {
        return new Object();
    }

    @Bean
    public Boolean cassandraSchemaInitializer() {
        System.out.println("ГОСПОДИ ВЫПОЛНИСЬ ДО КОНТЕКСТА, КТО ВООБЩЕ ПРИДУМАЛ ЛИКВИБЕЙЗ С КАСАНДРОЙ МЕШАТЬ");
        try (CqlSession tempSession = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter)
                .build()) {

            tempSession.execute(
                    "CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME +
                            " WITH replication = {'class':'SimpleStrategy','replication_factor':1};"
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to create keyspace via temporary session.", e);
        }

        runLiquibase();

        return true;
    }

    @Bean(destroyMethod = "close")
    @DependsOn("cassandraSchemaInitializer")
    public CqlSession cassandraSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter)
                .withKeyspace(KEYSPACE_NAME)
                .build();
    }

    private void runLiquibase() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:cassandra://" + contactPoints + ":" + port +
                            "/" + KEYSPACE_NAME + "?compliancemode=Liquibase&localdatacenter=" + localDatacenter,
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

            liquibase.update(new Contexts(), new LabelExpression(), false);
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException("Liquibase failed", e);
        }
    }

    /**
     * Костыль, передаём keyspace после инита
     */
    @Bean
    public CqlSessionBuilderCustomizer keyspaceCustomizer() {
        return builder -> builder.withKeyspace(KEYSPACE_NAME);
    }
}