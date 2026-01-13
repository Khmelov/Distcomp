package com.publick.service;

import com.publick.entity.Author;
import com.publick.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize with required first author
        Author firstAuthor = new Author();
        firstAuthor.setLogin("lis4aliss@yandex.by");
        firstAuthor.setPassword("password123"); // In real app, this should be hashed
        firstAuthor.setFirstname("Oneen");
        firstAuthor.setLastname("Федоренко");

        authorRepository.save(firstAuthor);
        System.out.println("Initialized first author with id: " + firstAuthor.getId());
    }
}