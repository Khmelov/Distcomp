package by.bsuir.task350.discussion.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(
            @Value("${spring.cassandra.keyspace-name}") String keyspaceName
    ) {
        return builder -> builder.withKeyspace(keyspaceName);
    }
}
