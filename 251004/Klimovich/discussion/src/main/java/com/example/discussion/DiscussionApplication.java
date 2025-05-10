package com.example.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableCassandraRepositories(basePackages = "com.example.discussion.repository")
public class DiscussionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }

}
