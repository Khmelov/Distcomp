package com.socialnetwork.config;

import com.socialnetwork.model.User;
import com.socialnetwork.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        System.out.println("DataInitializer: Checking database...");

        // Проверяем, существует ли уже пользователь с заданным логином
        if (!userRepository.existsByLogin("su582004@gmail.com")) {
            User user = new User();
            user.setLogin("su582004@gmail.com");
            user.setPassword("securepassword123");
            user.setFirstname("Domina");
            user.setLastname("Cympanosau");

            userRepository.save(user);

            System.out.println("DataInitializer: Created initial user with login: su582004@gmail.com");
        } else {
            System.out.println("DataInitializer: Initial user already exists");
        }

        System.out.println("DataInitializer: Total users in database: " + userRepository.count());
    }
}