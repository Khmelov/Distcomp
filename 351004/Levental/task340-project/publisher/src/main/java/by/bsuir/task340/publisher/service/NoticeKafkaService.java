package by.bsuir.task340.publisher.service;

import by.bsuir.task340.publisher.client.DiscussionNoticeClient;
import by.bsuir.task340.publisher.dto.NoticeKafkaMessage;
import by.bsuir.task340.publisher.dto.NoticeState;
import by.bsuir.task340.publisher.dto.response.NoticeResponseTo;
import by.bsuir.task340.publisher.kafka.PublisherNoticeProducer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeKafkaService {

    private final DiscussionNoticeClient discussionNoticeClient;
    private final PublisherNoticeProducer producer;

    public NoticeKafkaService(DiscussionNoticeClient discussionNoticeClient,
                              PublisherNoticeProducer producer) {
        this.discussionNoticeClient = discussionNoticeClient;
        this.producer = producer;
    }

    public List<NoticeKafkaMessage> findAll(Integer page, Integer size, String sort, String filter, Long articleId) {
        List<NoticeResponseTo> notices = discussionNoticeClient.findAll(page, size, sort, filter, articleId);

        return notices.stream()
                .map(this::toKafkaMessage)
                .toList();
    }

    public NoticeKafkaMessage findById(Long id) {
        NoticeResponseTo notice = discussionNoticeClient.findById(id);
        return toKafkaMessage(notice);
    }

    public NoticeKafkaMessage create(NoticeKafkaMessage request) {
        NoticeKafkaMessage message = new NoticeKafkaMessage(
                request.getId(),
                request.getArticleId(),
                request.getContent(),
                NoticeState.PENDING
        );

        producer.send(message);
        return message;
    }

    public NoticeKafkaMessage update(NoticeKafkaMessage request) {
        NoticeKafkaMessage message = new NoticeKafkaMessage(
                request.getId(),
                request.getArticleId(),
                request.getContent(),
                NoticeState.PENDING
        );

        producer.send(message);
        return message;
    }

    public void delete(Long id) {
        discussionNoticeClient.delete(id);
    }

    private NoticeKafkaMessage toKafkaMessage(NoticeResponseTo notice) {
        NoticeState state = NoticeState.PENDING;

        if (notice.getState() != null && !notice.getState().isBlank()) {
            state = NoticeState.valueOf(notice.getState().toUpperCase());
        }

        return new NoticeKafkaMessage(
                notice.getId(),
                notice.getArticleId(),
                notice.getContent(),
                state
        );
    }
}