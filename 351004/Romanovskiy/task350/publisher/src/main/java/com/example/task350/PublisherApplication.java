package com.example.task350; // Убедись, что пакет совпадает с корнем всех папок

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.task350") // Принудительно сканируем всё
@EnableCaching 
public class PublisherApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublisherApplication.class, args);
    }
}