package com.socialnetwork.discussion.config;

import com.socialnetwork.discussion.model.Message;
import com.socialnetwork.discussion.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

@Component("discussionDataInitializer") // Изменяем имя бина
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    @Autowired
    private MessageRepository messageRepository;

    @PostConstruct
    public void init() {
        System.out.println("Initializing Cassandra database...");

        // Очищаем существующие данные
        messageRepository.deleteAll();

        // Добавляем тестовые данные с различными странами
        Message message1 = new Message("US", 1L, 1L, "First test message from US");
        Message message2 = new Message("US", 1L, 2L, "Second test message from US");
        Message message3 = new Message("RU", 2L, 3L, "Test message from Russia");
        Message message4 = new Message("BY", 2L, 4L, "Test message from Belarus");

        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        messageRepository.save(message4);

        System.out.println("Cassandra database initialized with test data");
        System.out.println("Total messages: " + messageRepository.count());
    }
}