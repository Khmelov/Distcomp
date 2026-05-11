package com.github.Lexya06.startrestapp.discussion.api.dto.notice;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaNoticeResponseMessage implements Serializable {
    private NoticeResponseTo responsePayload;
    private List<NoticeResponseTo> responseListPayload;
    private String errorType;
    private String errorMessage;
    private String errorKey;
}
