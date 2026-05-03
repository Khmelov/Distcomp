package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.TopicRequestTo;
import by.bsuir.task310.dto.TopicResponseTo;
import by.bsuir.task310.model.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    public Topic toEntity(TopicRequestTo requestTo) {
        Topic topic = new Topic();
        topic.setId(requestTo.getId());
        topic.setAuthorId(requestTo.getAuthorId());
        topic.setTitle(requestTo.getTitle());
        topic.setContent(requestTo.getContent());
        return topic;
    }

    public TopicResponseTo toResponseTo(Topic topic) {
        TopicResponseTo responseTo = new TopicResponseTo();
        responseTo.setId(topic.getId());
        responseTo.setAuthorId(topic.getAuthorId());
        responseTo.setTitle(topic.getTitle());
        responseTo.setContent(topic.getContent());
        responseTo.setCreated(topic.getCreated());
        responseTo.setModified(topic.getModified());
        return responseTo;
    }
}