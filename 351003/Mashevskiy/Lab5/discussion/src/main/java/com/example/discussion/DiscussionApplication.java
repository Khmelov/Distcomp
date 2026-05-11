package com.example.discussion;

import com.example.discussion.controller.DiscussionNoteController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class DiscussionApplication {

    @Autowired
    private DiscussionNoteController noteController;

    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
        System.out.println("Discussion module started on port 24130");
    }

    @PostConstruct
    public void init() {
        noteController.deleteAllNotes();
        System.out.println("All notes cleared on startup");
    }
}