package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
		System.out.println("==========================================");
		System.out.println("Blog Application Started Successfully!");
		System.out.println("Server running on port: 24110");
		System.out.println("API Versions:");
		System.out.println("  - v1.0 (Unprotected): http://localhost:24110/api/v1.0");
		System.out.println("  - v2.0 (Protected):   http://localhost:24110/api/v2.0");
		System.out.println("==========================================");
	}
}