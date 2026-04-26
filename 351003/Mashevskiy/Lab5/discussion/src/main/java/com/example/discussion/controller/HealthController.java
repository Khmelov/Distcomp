package com.example.discussion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Discussion module is running on port 24130";
    }

    @GetMapping("/info")
    public String info() {
        return "Discussion module - Kafka moderator service\n" +
                "Listens to: InTopic\n" +
                "Sends to: OutTopic\n" +
                "Moderation: checks for bad words";
    }
}