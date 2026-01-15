    package com.blog.service.impl;

    import com.blog.dto.request.TopicRequestTo;
    import com.blog.dto.response.TopicResponseTo;
    import com.blog.mapper.TopicMapper;
    import com.blog.model.Topic;
    import com.blog.repository.TopicRepository;
    import com.blog.service.EditorService;
    import com.blog.service.TopicService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    public class TopicServiceImpl implements TopicService {

        @Autowired
        private TopicRepository topicRepository;

        @Autowired
        private TopicMapper topicMapper;

        @Autowired
        private EditorService editorService;

        @Override
        public List<TopicResponseTo> getAll() {
            return topicRepository.findAll().stream()
                    .map(topicMapper::toResponse)
                    .collect(Collectors.toList());
        }

        @Override
        public TopicResponseTo getById(Long id) {
            Topic topic = topicRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
            return topicMapper.toResponse(topic);
        }

        @Override
        public TopicResponseTo create(TopicRequestTo request) {
            // Проверяем существование редактора
            if (!editorService.existsById(request.getWriterId())) {
                throw new RuntimeException("Editor not found with id: " + request.getWriterId());
            }

            Topic topic = topicMapper.toEntity(request);
            Topic saved = topicRepository.save(topic);
            return topicMapper.toResponse(saved);
        }

        @Override
        public TopicResponseTo update(Long id, TopicRequestTo request) {
            if (!topicRepository.existsById(id)) {
                throw new RuntimeException("Topic not found with id: " + id);
            }

            // Проверяем существование редактора
            if (!editorService.existsById(request.getWriterId())) {
                throw new RuntimeException("Editor not found with id: " + request.getWriterId());
            }

            Topic topic = topicMapper.toEntity(request);
            topic.setId(id);
            Topic updated = topicRepository.update(topic);
            return topicMapper.toResponse(updated);
        }

        @Override
        public void delete(Long id) {
            if (!topicRepository.deleteById(id)) {
                throw new RuntimeException("Topic not found with id: " + id);
            }
        }

        @Override
        public boolean existsById(Long id) {
            return topicRepository.existsById(id);
        }

        @Override
        public List<TopicResponseTo> getByEditorId(Long editorId) {
            return topicRepository.findByEditorId(editorId).stream()
                    .map(topicMapper::toResponse)
                    .collect(Collectors.toList());
        }

        @Override
        public List<TopicResponseTo> getByTagId(Long tagId) {
            return topicRepository.findByTagIdsContaining(tagId).stream()
                    .map(topicMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }