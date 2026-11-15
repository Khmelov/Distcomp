
package com.example.task330.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

// Исключаем автоматическое создание обычного SQL DataSource.
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DiscussionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }
}
