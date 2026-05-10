package com.github.Lexya06.startrestapp.discussion.api.dto.notice;

import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaNoticeMessage implements Serializable {
    private OperationType operation;
    private NoticeRequestTo requestPayload;
    private Long id;
    private NoticeKeyDto keyDto;
    private NoticeSearchCriteria criteria;
}
