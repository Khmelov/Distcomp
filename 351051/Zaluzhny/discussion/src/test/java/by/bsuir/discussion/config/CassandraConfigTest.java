package by.bsuir.discussion.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import liquibase.integration.spring.SpringLiquibase;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CassandraConfigTest {

    @Test
    @DisplayName("JDBC-адрес Cassandra совпадает с префиксом jdbc:cassandra и схемой distcomp")
    void jdbcUrlUsesTaskPrefix() {
        assertThat(CassandraLiquibaseConfig.jdbcUrl("localhost", 9042, "distcomp", "datacenter1"))
                .isEqualTo("jdbc:cassandra://localhost:9042/distcomp?localdatacenter=datacenter1&compliancemode=Liquibase");
    }

    @Test
    @DisplayName("Источник Liquibase указывает на keyspace, не открывая соединение")
    void dataSourceIsConfiguredBeforeConnect() {
        CassandraLiquibaseConfig config = new CassandraLiquibaseConfig();
        DataSource dataSource = config.cassandraDataSource("127.0.0.1", 9042, "datacenter1", "distcomp");

        assertThat(dataSource).isInstanceOf(DriverManagerDataSource.class);
        DriverManagerDataSource driverManager = (DriverManagerDataSource) dataSource;
        assertThat(driverManager.getUrl()).startsWith("jdbc:cassandra://127.0.0.1:9042/distcomp");
        assertThat(driverManager.getUsername()).isNull();

        SpringLiquibase liquibase = config.cassandraLiquibase(dataSource, "distcomp");
        assertThat(liquibase.getChangeLog()).isEqualTo("classpath:db/changelog/db.changelog-master.xml");
        assertThat(liquibase.getDefaultSchema()).isEqualTo("distcomp");
    }

    @Test
    @DisplayName("Имя keyspace с посторонними символами отклоняется до подключения")
    void rejectsUnsafeKeyspace() {
        assertThatThrownBy(() -> CassandraKeyspaceInitializer.ensureKeyspace("localhost", 9042, "datacenter1", "distcomp;drop"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
