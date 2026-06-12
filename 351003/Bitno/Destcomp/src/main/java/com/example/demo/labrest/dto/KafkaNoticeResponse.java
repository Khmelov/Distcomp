package com.example.demo.labrest.dto;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaNoticeResponse implements Serializable {

    public enum State {
        SUCCESS,
        NOT_FOUND,
        ERROR,
        APPROVE,
        DECLINE
    }

    private String correlationId;
    private State state;
    private String reason;

    private Long id;
    private Long topicId;
    private String content;

    private List<NoticeData> notices;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeData implements Serializable {
        private Long id;
        private Long topicId;
        private String content;
    }
}