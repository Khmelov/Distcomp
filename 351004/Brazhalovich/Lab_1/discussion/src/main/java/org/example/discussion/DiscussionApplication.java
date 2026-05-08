package org.example.discussion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class
})
public class DiscussionApplication implements CommandLineRunner {

    private final CassandraTemplate cassandraTemplate;

    public DiscussionApplication(CassandraTemplate cassandraTemplate) {
        this.cassandraTemplate = cassandraTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("schema.cql");
        byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String cql = new String(bdata, StandardCharsets.UTF_8);
        String[] statements = cql.split(";");
        for (String stmt : statements) {
            String trimmed = stmt.trim();
            if (trimmed.isEmpty()) continue;
            try {
                cassandraTemplate.getCqlOperations().execute(trimmed);
            } catch (Exception e) {
                // Игнорируем ошибку "already exists" только для ALTER TABLE ADD
                if (trimmed.toUpperCase().contains("ALTER TABLE") &&
                        e.getMessage().contains("already exists")) {
                    System.out.println("Column already exists, ignoring: " + trimmed);
                } else {
                    throw e;
                }
            }
        }
    }
}