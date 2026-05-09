package com.example.Labs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LabsApplication {
	public static void main(String[] args) {
		SpringApplication.run(LabsApplication.class, args);
	}
}