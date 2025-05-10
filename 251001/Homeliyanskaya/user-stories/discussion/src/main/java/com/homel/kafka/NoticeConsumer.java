package com.homel.kafka;

import com.homel.dto.NoticeRequestTo;
import com.homel.service.NoticeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NoticeConsumer {
    private final NoticeService noticeService;
    private final NoticeProducer noticeProducer;

    public NoticeConsumer(NoticeService noticeService, NoticeProducer noticeProducer) {
        this.noticeService = noticeService;
        this.noticeProducer = noticeProducer;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void processNotice(NoticeRequestTo notice) {
        notice.setState(moderate(notice.getContent()));
        noticeService.createNotice(notice);
    }

    private String moderate(String text) {
        return text.contains("badword") ? "DECLINE" : "APPROVE";
    }
}
