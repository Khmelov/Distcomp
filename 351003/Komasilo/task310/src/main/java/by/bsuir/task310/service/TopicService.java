package by.bsuir.task310.service;

import by.bsuir.task310.dto.TopicRequestTo;
import by.bsuir.task310.dto.TopicResponseTo;
import by.bsuir.task310.exception.EntityNotFoundException;
import by.bsuir.task310.mapper.TopicMapper;
import by.bsuir.task310.model.Topic;
import by.bsuir.task310.repository.AuthorRepository;
import by.bsuir.task310.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final AuthorRepository authorRepository;
    private final TopicMapper mapper;

    public TopicService(TopicRepository topicRepository, AuthorRepository authorRepository, TopicMapper mapper) {
        this.topicRepository = topicRepository;
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    public TopicResponseTo create(TopicRequestTo requestTo) {
        if (!authorRepository.existsById(requestTo.getAuthorId())) {
            throw new EntityNotFoundException("Author not found");
        }

        Topic topic = mapper.toEntity(requestTo);
        String now = LocalDateTime.now().toString();
        topic.setCreated(now);
        topic.setModified(now);

        return mapper.toResponseTo(topicRepository.save(topic));
    }

    public List<TopicResponseTo> getAll() {
        return topicRepository.findAll()
                .stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    public TopicResponseTo getById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        return mapper.toResponseTo(topic);
    }

    public TopicResponseTo update(TopicRequestTo requestTo) {
        if (!topicRepository.existsById(requestTo.getId())) {
            throw new EntityNotFoundException("Topic not found");
        }

        if (!authorRepository.existsById(requestTo.getAuthorId())) {
            throw new EntityNotFoundException("Author not found");
        }

        Topic oldTopic = topicRepository.findById(requestTo.getId()).orElseThrow();
        Topic topic = mapper.toEntity(requestTo);
        topic.setCreated(oldTopic.getCreated());
        topic.setModified(LocalDateTime.now().toString());

        return mapper.toResponseTo(topicRepository.update(topic));
    }

    public void delete(Long id) {
        if (!topicRepository.deleteById(id)) {
            throw new EntityNotFoundException("Topic not found");
        }
    }
}