package com.task.rest.config;

import com.task.rest.model.Comment;
import com.task.rest.model.Mark;
import com.task.rest.model.Tweet;
import com.task.rest.model.Writer;
import com.task.rest.repository.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public InMemoryRepository<Writer> writerRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryRepository<Tweet> tweetRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryRepository<Mark> markRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryRepository<Comment> commentRepository() {
        return new InMemoryRepository<>();
    }
}