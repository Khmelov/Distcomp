package com.homel.user_stories.kafka;

import com.homel.user_stories.dto.NoticeResponseTo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class NoticeConsumer {

    private final BlockingQueue<NoticeResponseTo> responseQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void receiveResponse(NoticeResponseTo response) {
        responseQueue.offer(response);
    }

    public NoticeResponseTo getResponse() {
        try {
            NoticeResponseTo response = responseQueue.poll(5, TimeUnit.SECONDS); // Ждем 5 секунд
            if (response == null) {
                throw new RuntimeException("Ошибка: Не получен ответ от Kafka");
            }
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ошибка при получении ответа", e);
        }
    }

    public List<NoticeResponseTo> getResponseList() {
        try {
            NoticeResponseTo response = responseQueue.poll(5, TimeUnit.SECONDS); // Ждем 5 секунд
            if (response == null) {
                throw new RuntimeException("Ошибка: Не получен список ответов от Kafka");
            }
            return List.of(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ошибка при получении списка ответов", e);
        }
    }
}