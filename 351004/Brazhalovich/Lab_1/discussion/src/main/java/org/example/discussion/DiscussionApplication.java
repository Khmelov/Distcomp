package org.example.discussion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
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
        // Выполняем schema.cql для создания keyspace и таблицы, если их нет
        ClassPathResource resource = new ClassPathResource("schema.cql");
        byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String cql = new String(bdata, StandardCharsets.UTF_8);
        // Разделяем на отдельные выражения и выполняем
        String[] statements = cql.split(";");
        for (String stmt : statements) {
            if (!stmt.trim().isEmpty()) {
                cassandraTemplate.getCqlOperations().execute(stmt);
            }
        }
    }
}