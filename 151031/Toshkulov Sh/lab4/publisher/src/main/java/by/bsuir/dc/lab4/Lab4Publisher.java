package by.bsuir.dc.lab4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab4Publisher {

	@Autowired
	private KafkaConfig config;

	public static void main(String[] args) {
		SpringApplication.run(Lab4Publisher.class, args);
	}

}
