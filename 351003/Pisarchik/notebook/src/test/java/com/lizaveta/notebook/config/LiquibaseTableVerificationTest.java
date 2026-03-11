package com.lizaveta.notebook.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
class LiquibaseTableVerificationTest extends PostgresTestContainerConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void liquibase_ShouldCreateRequiredTablesInDistcompSchema() {
        String sql = """
                SELECT table_name FROM information_schema.tables
                WHERE table_schema = 'distcomp' AND table_name IN ('tbl_writer', 'tbl_story', 'tbl_marker', 'tbl_notice')
                ORDER BY table_name
                """;
        List<String> tables = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("table_name"));
        assertThat(tables, hasSize(4));
        assertThat(tables, hasItems("tbl_writer", "tbl_story", "tbl_marker", "tbl_notice"));
    }
}
