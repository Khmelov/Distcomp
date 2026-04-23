package by.tracker.rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistcompApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistcompApplication.class, args);
        System.out.println("Server started on http://localhost:24110");
    }
}