package by.bsuir.dc.lab5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab5Publisher {

	@Autowired
	private KafkaConfig config;

	public static void main(String[] args) {
		SpringApplication.run(Lab5Publisher.class, args);
	}

}
