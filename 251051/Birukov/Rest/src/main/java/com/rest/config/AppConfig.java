package com.rest.config;

import com.rest.repository.inmemory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {
    
    @Bean
    @Primary
    public InMemoryWriterRepository writerRepository() {
        return new InMemoryWriterRepository();
    }
    
    @Bean
    @Primary
    public InMemoryTweetRepository tweetRepository() {
        return new InMemoryTweetRepository();
    }
    
    @Bean
    @Primary
    public InMemoryNoteRepository noteRepository() {
        return new InMemoryNoteRepository();
    }
    
    @Bean
    @Primary
    public InMemoryLabelRepository labelRepository() {
        return new InMemoryLabelRepository();
    }
}