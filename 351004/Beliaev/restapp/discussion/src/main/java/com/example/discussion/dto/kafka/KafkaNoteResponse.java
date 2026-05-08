package com.example.discussion.dto.kafka; // В discussion поменяй на com.example.discussion.dto.kafka

import com.example.discussion.dto.response.NoteResponseTo; // В discussion поменяй импорт
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaNoteResponse {
    private NoteResponseTo note;
    private List<NoteResponseTo> notes;
    private String error;
}