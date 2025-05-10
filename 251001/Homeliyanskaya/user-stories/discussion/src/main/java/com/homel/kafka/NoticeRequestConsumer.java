package com.homel.kafka;

import com.homel.dto.NoticeMethodRequestTo;
import com.homel.dto.NoticeRequestTo;
import com.homel.dto.NoticeResponseTo;
import com.homel.service.NoticeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeRequestConsumer {
    private final NoticeService noticeService;
    private final NoticeProducer noticeProducer;

    public NoticeRequestConsumer(NoticeService noticeService, NoticeProducer noticeProducer) {
        this.noticeService = noticeService;
        this.noticeProducer = noticeProducer;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void processRequest(NoticeMethodRequestTo request) {
        try {
            switch (request.getMethod()) {
                case "GET":
                    NoticeResponseTo notice = noticeService.getNotice(request.getId());
                    noticeProducer.sendToOutTopic(notice);
                    break;
                case "GET_ALL":
                    List<NoticeResponseTo> notices = noticeService.getAllNotices();
                    noticeProducer.sendToOutTopic(notices);
                    break;
                case "PUT":
                    NoticeResponseTo updatedNotice = noticeService.updateNotice(new NoticeRequestTo(request.getId(), request.getState(), request.getStoryId(), request.getContent()));
                    noticeProducer.sendToOutTopic(updatedNotice);
                    break;
                case "DELETE":
                    noticeService.deleteNotice(request.getId());
                    noticeProducer.sendToOutTopic("Deleted: " + request.getId());
                    break;
            }
        } catch (Exception e) {
            noticeProducer.sendToOutTopic("Error: " + e.getMessage());
        }
    }
}
