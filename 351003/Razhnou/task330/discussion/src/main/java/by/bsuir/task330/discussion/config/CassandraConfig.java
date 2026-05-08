package by.bsuir.task330.discussion.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer() {
        return builder -> builder.withKeyspace("distcomp");
    }
}
