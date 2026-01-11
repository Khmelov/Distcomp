package by.rest.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {
    CassandraAutoConfiguration.class,
    CassandraDataAutoConfiguration.class,
    CassandraRepositoriesAutoConfiguration.class
})
@EnableKafka
@RestController
public class DiscussionApp {
    
    public static void main(String[] args) {
        SpringApplication.run(DiscussionApp.class, args);
    }
    
    @GetMapping("/health")
    public String health() {
        return "Discussion Module (Kafka only) is running on port 24130!";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Ready for Kafka testing!";
    }
}