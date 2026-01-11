package by.rest.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
@RestController  
public class PublisherApp {
    
    public static void main(String[] args) {
        SpringApplication.run(PublisherApp.class, args);
    }
    
    @GetMapping("/health")  
    public String health() {
        return "Publisher module is running on port 24110!";
    }
}