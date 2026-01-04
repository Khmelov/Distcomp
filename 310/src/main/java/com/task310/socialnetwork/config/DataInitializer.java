package com.task310.socialnetwork.config;

import com.task310.socialnetwork.model.User;
import com.task310.socialnetwork.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (userRepository.findAll().isEmpty()) {
            User user = new User();
            user.setLogin("su582004@gmail.com");
            user.setPassword("pas123");
            user.setFirstname("Полина");
            user.setLastname("Супранович");

            userRepository.save(user);
            System.out.println("Тестовый пользователь создан: " + user.getLogin());
        }
    }
}