package by.bsuir.task310;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Task310Application {

	public static void main(String[] args) {
		SpringApplication.run(Task310Application.class, args);
	}

}