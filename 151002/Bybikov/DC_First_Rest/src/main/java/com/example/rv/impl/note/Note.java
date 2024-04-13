package com.example.rv.impl.note;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Note {
    Long id;
    Long issueId;
    String content;
}
