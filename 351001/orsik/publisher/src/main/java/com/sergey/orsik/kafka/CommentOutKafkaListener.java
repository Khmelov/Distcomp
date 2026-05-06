package com.sergey.orsik.kafka;

import com.sergey.orsik.dto.kafka.CommentTransportReply;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CommentOutKafkaListener {

    private final CommentReplyWaitRegistry replyWaitRegistry;

    public CommentOutKafkaListener(CommentReplyWaitRegistry replyWaitRegistry) {
        this.replyWaitRegistry = replyWaitRegistry;
    }

    @KafkaListener(
            topics = "${kafka.topic.out:OutTopic}",
            containerFactory = "commentReplyKafkaListenerContainerFactory")
    public void onOutbound(CommentTransportReply reply) {
        replyWaitRegistry.complete(reply);
    }
}
