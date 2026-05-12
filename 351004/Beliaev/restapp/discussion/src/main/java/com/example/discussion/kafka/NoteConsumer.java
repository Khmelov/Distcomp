package com.example.discussion.kafka;

import com.example.discussion.dto.kafka.KafkaNoteRequest;
import com.example.discussion.dto.kafka.KafkaNoteResponse;
import com.example.discussion.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteConsumer {

    private final NoteService noteService;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    @SendTo("OutTopic") // Автоматически отправляет return value в OutTopic
    public KafkaNoteResponse listen(KafkaNoteRequest req) {
        try {
            switch (req.getOperation()) {
                case "CREATE":
                    // Модерация: если есть "bad", то DECLINE, иначе APPROVE
                    String state = req.getRequest().getContent().toLowerCase().contains("bad") ? "DECLINE" : "APPROVE";
                    noteService.createWithId(req.getGeneratedId(), req.getRequest(), state);
                    return null; // Ничего не шлем в OutTopic для POST!

                case "GET":
                    return new KafkaNoteResponse(noteService.getById(req.getNoteId()), null, null);

                case "GET_ALL":
                    return new KafkaNoteResponse(null, noteService.getAll(), null);

                case "UPDATE":
                    return new KafkaNoteResponse(noteService.update(req.getNoteId(), req.getRequest()), null, null);

                case "DELETE":
                    noteService.delete(req.getNoteId());
                    return new KafkaNoteResponse(); // Пустой ответ = успех

                default:
                    return new KafkaNoteResponse(null, null, "Unknown operation");
            }
        } catch (Exception e) {
            return new KafkaNoteResponse(null, null, e.getMessage()); // Возвращаем ошибку
        }
    }
}