package com.blog.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//@Configuration
public class CassandraSchemaConfig {

    @Autowired
    private CqlSession cqlSession;

    @PostConstruct
    public void initSchema() throws IOException {
        // Загружаем схему из файла
        ClassPathResource resource = new ClassPathResource("schema.cql");
        String schema = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Разделяем команды по точкам с запятой
        String[] commands = schema.split(";");

        for (String command : commands) {
            String trimmedCommand = command.trim();
            if (!trimmedCommand.isEmpty()) {
                cqlSession.execute(trimmedCommand);
            }
        }

        System.out.println("Схема Cassandra успешно инициализирована");
    }
}