package by.bsuir.task310.service;

import by.bsuir.task310.dto.TopicRequestTo;
import by.bsuir.task310.dto.TopicResponseTo;
import by.bsuir.task310.exception.DuplicateException;
import by.bsuir.task310.exception.EntityNotFoundException;
import by.bsuir.task310.mapper.TopicMapper;
import by.bsuir.task310.model.Label;
import by.bsuir.task310.model.Topic;
import by.bsuir.task310.repository.AuthorRepository;
import by.bsuir.task310.repository.LabelRepository;
import by.bsuir.task310.repository.TopicRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final AuthorRepository authorRepository;
    private final LabelRepository labelRepository;
    private final TopicMapper mapper;

    public TopicService(
            TopicRepository topicRepository,
            AuthorRepository authorRepository,
            LabelRepository labelRepository,
            TopicMapper mapper
    ) {
        this.topicRepository = topicRepository;
        this.authorRepository = authorRepository;
        this.labelRepository = labelRepository;
        this.mapper = mapper;
    }

    public TopicResponseTo create(TopicRequestTo requestTo) {
        if (topicRepository.existsByTitle(requestTo.getTitle())) {
            throw new DuplicateException("Topic with this title already exists");
        }

        if (!authorRepository.existsById(requestTo.getAuthorId())) {
            throw new EntityNotFoundException("Author not found");
        }

        Topic topic = mapper.toEntity(requestTo);
        LocalDateTime now = LocalDateTime.now();
        topic.setCreated(now);
        topic.setModified(now);

        Topic saved = topicRepository.save(topic);

        if (requestTo.getLabels() != null) {
            for (String labelName : requestTo.getLabels()) {
                Label label = new Label();
                label.setName(labelName);
                label.setTopicId(saved.getId());
                labelRepository.save(label);
            }
        }

        return mapper.toResponseTo(saved);
    }

    public List<TopicResponseTo> getAll() {
        return topicRepository.findAll()
                .stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Cacheable(value = "topics", key = "#id")
    public TopicResponseTo getById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        return mapper.toResponseTo(topic);
    }

    @CachePut(value = "topics", key = "#requestTo.id")
    public TopicResponseTo update(TopicRequestTo requestTo) {
        if (!topicRepository.existsById(requestTo.getId())) {
            throw new EntityNotFoundException("Topic not found");
        }

        if (!authorRepository.existsById(requestTo.getAuthorId())) {
            throw new EntityNotFoundException("Author not found");
        }

        Topic oldTopic = topicRepository.findById(requestTo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        Topic topic = mapper.toEntity(requestTo);
        topic.setCreated(oldTopic.getCreated());
        topic.setModified(LocalDateTime.now());

        Topic updated = topicRepository.save(topic);
        return mapper.toResponseTo(updated);
    }

    @CacheEvict(value = "topics", key = "#id")
    public void delete(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new EntityNotFoundException("Topic not found");
        }

        labelRepository.deleteByTopicId(id);
        topicRepository.deleteById(id);
    }
}