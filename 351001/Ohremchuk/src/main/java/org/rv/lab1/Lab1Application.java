package org.rv.lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class Lab1Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab1Application.class, args);
    }

}
