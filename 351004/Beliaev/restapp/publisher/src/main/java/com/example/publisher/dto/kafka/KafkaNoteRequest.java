package com.example.publisher.dto.kafka; // В discussion поменяй на com.example.discussion.dto.kafka

import com.example.publisher.dto.request.NoteRequestTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaNoteRequest {
    private String operation; // Варианты: CREATE, GET, GET_ALL, UPDATE, DELETE
    private Long noteId;      // ID заметки (для GET, PUT, DELETE)
    private NoteRequestTo request; // Тело запроса (для CREATE, PUT)
    private Long generatedId; // Сгенерированный ID на стороне publisher (для CREATE)
}