package com.example.discussion.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document(collection = "database_sequences")
public class DatabaseSequence {
    @Id
    private String id;
    private Long value;


}