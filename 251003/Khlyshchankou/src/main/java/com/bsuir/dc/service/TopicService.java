package com.bsuir.dc.service;

import com.bsuir.dc.dto.request.TopicRequestTo;
import com.bsuir.dc.dto.response.TopicResponseTo;
import com.bsuir.dc.model.Author;
import com.bsuir.dc.model.Label;
import com.bsuir.dc.model.Topic;
import com.bsuir.dc.repository.AuthorRepository;
import com.bsuir.dc.repository.LabelRepository;
import com.bsuir.dc.repository.TopicRepository;
import com.bsuir.dc.util.exception.EntityNotFoundException;
import com.bsuir.dc.util.mapper.LabelMapper;
import com.bsuir.dc.util.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final AuthorRepository authorRepository;
    private final TopicMapper topicMapper;
    private final LabelMapper labelMapper;
    private final LabelRepository labelRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository, AuthorRepository authorRepository, TopicMapper topicMapper,
                           LabelMapper labelMapper, LabelRepository labelRepository) {
        this.topicRepository = topicRepository;
        this.authorRepository = authorRepository;
        this.topicMapper = topicMapper;
        this.labelMapper = labelMapper;
        this.labelRepository = labelRepository;
    }

    @Transactional
    public TopicResponseTo save(TopicRequestTo topicRequestTo) {
        Topic topic = topicMapper.toTopic(topicRequestTo);
        setCreator(topic, topicRequestTo.getAuthorId());
        if (!topicRequestTo.getLabels().isEmpty())
            saveLabels(topic, topicRequestTo.getLabels().stream().map(Label::new).toList());
        topic.setCreated(new Date());
        topic.setModified(new Date());
        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }

    private void saveLabels(Topic topic, List<Label> labels){
        Set<String> labelNames = labels.stream().map(Label::getName).collect(Collectors.toSet());
        List<Label> existingLabels = labelRepository.findByNameIn(labelNames);

        Set<String> existingLabelNames = existingLabels.stream().map(Label::getName).collect(Collectors.toSet());
        List<Label> newLabels = labels.stream()
                .filter(label -> !existingLabelNames.contains(label.getName()))
                .collect(Collectors.toList());

        if (!newLabels.isEmpty()) { labelRepository.saveAll(newLabels); }
        existingLabels.addAll(newLabels);
        topic.setLabels(existingLabels);

        for (Label label : existingLabels) {
            label.getTopics().add(topic);
        }
    }

    private void setCreator(Topic topic, long authorId){
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new EntityNotFoundException("Author with such id does not exist"));
        topic.setCreator(author);
    }

    @Transactional(readOnly = true)
    public List<TopicResponseTo> findAll() {
        return topicMapper.toTopicResponseList(topicRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TopicResponseTo findById(long id) {
        return topicMapper.toTopicResponse(
                topicRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Topic with such id does not exist")));
    }

    @Transactional
    public TopicResponseTo update(TopicRequestTo topicRequestTo) {
        Topic topic = topicMapper.toTopic(topicRequestTo);
        Topic oldTopic = topicRepository.findById(topic.getId()).orElseThrow(() -> new EntityNotFoundException("Old topic not found"));
        Long authorId = topicRequestTo.getAuthorId();

        if (authorId != null) { setCreator(topic, authorId); }

        topic.setCreated(oldTopic.getCreated());
        topic.setModified(new Date());
        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }

    @Transactional
    public void deleteById(long id) {
        if (!topicRepository.existsById(id)) { throw new EntityNotFoundException("Topic with such id does not exist"); }
        topicRepository.deleteById(id);
    }

    public boolean existsByTitle(String title){
        return topicRepository.existsByTitle(title);
    }
}
