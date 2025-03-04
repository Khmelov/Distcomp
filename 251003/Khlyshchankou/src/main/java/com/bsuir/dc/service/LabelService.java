package com.bsuir.dc.service;

import com.bsuir.dc.dto.request.LabelRequestTo;
import com.bsuir.dc.dto.response.LabelResponseTo;
import com.bsuir.dc.model.Label;
import com.bsuir.dc.model.Topic;
import com.bsuir.dc.repository.LabelRepository;
import com.bsuir.dc.repository.TopicRepository;
import com.bsuir.dc.util.exception.EntityNotFoundException;
import com.bsuir.dc.util.mapper.LabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    private final LabelRepository labelRepository;
    private final TopicRepository topicRepository;
    private final LabelMapper labelMapper;

    @Autowired
    public LabelService(LabelRepository labelRepository, TopicRepository topicRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.topicRepository = topicRepository;
        this.labelMapper = labelMapper;
    }

    public LabelResponseTo save(Label label, long articleId) {
        Topic topic = topicRepository.findById(articleId).orElseThrow(() -> new EntityNotFoundException("Topic with such id does not exist"));
        topic.getLabels().add(label);
        label.getTopics().add(topic);
        return labelMapper.toLabelResponse(labelRepository.save(label));
    }

    public LabelResponseTo save(LabelRequestTo labelRequestTo) {
        Label label = labelMapper.toLabel(labelRequestTo);
        return labelMapper.toLabelResponse(labelRepository.save(label));
    }

    public List<LabelResponseTo> findAll() {
        return labelMapper.toLabelResponseList(labelRepository.findAll());
    }

    public LabelResponseTo findById(long id) {
        return labelMapper.toLabelResponse(labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label with such id does not exist")));
    }

    public void deleteById(long id) {
        if (!labelRepository.existsById(id))
            throw new EntityNotFoundException("Label with such id not found");
        labelRepository.deleteById(id);
    }

    public LabelResponseTo update(LabelRequestTo labelRequestTo) {
        Label label = labelMapper.toLabel(labelRequestTo);
        return labelMapper.toLabelResponse(labelRepository.save(label));
    }

    public boolean existsByName(String name) { return labelRepository.existsByName(name); }
}
