package com.group310971.gormash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseTo {

    private Long id;
    private Long topicId;
    private String content;
}
