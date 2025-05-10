package com.homel.user_stories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.homel.user_stories")
public class UserStoriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserStoriesApplication.class, args);
	}

}
