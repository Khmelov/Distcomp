package by.bsuir.discussion.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class CassandraLiquibaseConfig {

    public static final String JDBC_DRIVER = "com.ing.data.cassandra.jdbc.CassandraDriver";

    @Bean
    public DataSource cassandraDataSource(
            @Value("${spring.cassandra.contact-points}") String contactPoints,
            @Value("${spring.cassandra.port}") int port,
            @Value("${spring.cassandra.local-datacenter}") String datacenter,
            @Value("${spring.cassandra.keyspace-name}") String keyspace) {
        String host = contactPoints.split(",")[0].trim();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JDBC_DRIVER);
        dataSource.setUrl(jdbcUrl(host, port, keyspace, datacenter));
        return dataSource;
    }

    @Bean(name = "cassandraLiquibase")
    public SpringLiquibase cassandraLiquibase(
            DataSource cassandraDataSource,
            @Value("${spring.cassandra.keyspace-name}") String keyspace) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(cassandraDataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setDefaultSchema(keyspace);
        liquibase.setLiquibaseSchema(keyspace);
        return liquibase;
    }

    public static String jdbcUrl(String host, int port, String keyspace, String datacenter) {
        return "jdbc:cassandra://" + host + ":" + port + "/" + keyspace
                + "?localdatacenter=" + datacenter + "&compliancemode=Liquibase";
    }
}
