package by.bsuir.task340.discussion.service;

import by.bsuir.task340.discussion.dto.NoticeKafkaMessage;
import by.bsuir.task340.discussion.dto.NoticeState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeModerationService {
    private static final List<String> BAD_WORDS = List.of("spam", "bad", "xxx");

    public NoticeKafkaMessage process(NoticeKafkaMessage message) {
        String text = message.getContent() == null ? "" : message.getContent().toLowerCase();
        boolean rejected = BAD_WORDS.stream().anyMatch(text::contains);
        message.setState(rejected ? NoticeState.DECLINE : NoticeState.APPROVE);
        // TODO: сохранить Notice в Cassandra здесь
        return message;
    }
}
