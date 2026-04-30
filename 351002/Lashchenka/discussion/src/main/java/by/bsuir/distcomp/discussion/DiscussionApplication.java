package by.bsuir.distcomp.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableCassandraRepositories(basePackages = "by.bsuir.distcomp.discussion.cassandra")
public class DiscussionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }
}
