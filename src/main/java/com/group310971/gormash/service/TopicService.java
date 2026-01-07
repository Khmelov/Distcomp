package com.group310971.gormash.service;

import com.group310971.gormash.dto.TopicRequestTo;
import com.group310971.gormash.dto.TopicResponseTo;
import com.group310971.gormash.mapper.TopicMapper;
import com.group310971.gormash.model.Editor;
import com.group310971.gormash.model.Mark;
import com.group310971.gormash.model.Topic;
import com.group310971.gormash.model.TopicMark;
import com.group310971.gormash.repository.EditorRepository;
import com.group310971.gormash.repository.MarkRepository;
import com.group310971.gormash.repository.TopicMarkRepository;
import com.group310971.gormash.repository.TopicRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final EditorRepository editorRepository;
    private final MarkRepository markRepository;
    private final TopicMarkRepository topicMarkRepository;
    private final TopicMapper topicMapper = TopicMapper.INSTANCE;

    public TopicResponseTo createTopic(@Valid TopicRequestTo topicRequestTo){
        Topic topic = topicMapper.toEntity(topicRequestTo);
        Optional<Editor> optional = editorRepository.findById(topicRequestTo.getEditorId());
        if (optional.isEmpty())
            throw new RuntimeException("Topic not exists");
        topic.setEditor(optional.get());
        Topic savedTopic = topicRepository.save(topic);
        if (topicRequestTo.getMarks() != null)
            for (String markName : topicRequestTo.getMarks()){
                Mark mark = new Mark();
                mark.setName(markName);
                TopicMark topicMark = new TopicMark();
                topicMark.setTopic(savedTopic);
                topicMark.setMark(markRepository.save(mark));
                topicMarkRepository.save(topicMark);
            }
        return topicMapper.toResponse(savedTopic);
    }

    public TopicResponseTo updateTopic(Long id, @Valid TopicRequestTo topicRequestTo){
        if (id == null)
            id = topicRequestTo.getId();
        else
            topicRequestTo.setId(id);
        if (id == null) {
            throw new RuntimeException("Topic id cannot be null for update");
        }
        var optional = topicRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Topic not exists");
        Optional<Editor> editorOptional = editorRepository.findById(topicRequestTo.getEditorId());
        if (editorOptional.isEmpty())
            throw new RuntimeException("Topic not exists");
        Topic persistence = optional.get();
        Topic topic = topicMapper.toEntity(topicRequestTo);
        persistence.setContent(topic.getContent());
        persistence.setEditor(editorOptional.get());
        persistence.setTitle(topic.getTitle());
        Topic updatedTopic = topicRepository.save(persistence);
        return topicMapper.toResponse(updatedTopic);
    }

    public TopicResponseTo getTopicById(Long id) {
        return topicRepository.findById(id)
                .map(topicMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
    }

    public List<TopicResponseTo> getAllTopics(){
        LinkedList<TopicResponseTo> list = new LinkedList<>();
        for (Topic topic : topicRepository.findAll()){
            list.add(topicMapper.toResponse(topic));
        }
        return list;
    }

    public TopicResponseTo deleteTopic(Long id) {
        var optional = topicRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Topic not exists");
        Topic topic = optional.get();
        for (TopicMark topicMark : topicMarkRepository.findByTopic(topic)) {
            topicMarkRepository.delete(topicMark);
            markRepository.delete(topicMark.getMark());
        }
        topicRepository.delete(topic);
        return topicMapper.toResponse(topic);
    }
}
