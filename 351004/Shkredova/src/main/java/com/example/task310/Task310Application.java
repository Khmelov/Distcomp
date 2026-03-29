package com.example.task310;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Task310Application {
    public static void main(String[] args) {
        SpringApplication.run(Task310Application.class, args);
    }
}