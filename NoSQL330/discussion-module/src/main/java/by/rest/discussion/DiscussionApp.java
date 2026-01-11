package by.rest.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DiscussionApp {
    
    public static void main(String[] args) {
        SpringApplication.run(DiscussionApp.class, args);
    }
    
    @GetMapping("/health")
    public String health() {
        return "Discussion module is running on port 24130!";
    }
}