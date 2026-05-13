package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
public class DistcompApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistcompApplication.class, args);
	}

}
