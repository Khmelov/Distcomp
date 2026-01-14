package com.blog.mapper;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.model.Editor;
import com.blog.model.Tag;
import com.blog.model.Topic;
import com.blog.repository.EditorRepository;
import com.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TopicMapper {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private TagRepository tagRepository;

    public Topic toEntity(TopicRequestTo request) {
        if (request == null) {
            return null;
        }

        Topic topic = new Topic();

        // Находим редактора
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new IllegalArgumentException("Editor not found with id: " + request.getEditorId()));
        topic.setEditor(editor);

        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());

        // Добавляем теги, если они указаны
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
                tags.add(tag);
            }
            topic.setTags(tags);
        }

        return topic;
    }

    public TopicResponseTo toResponse(Topic entity) {
        if (entity == null) {
            return null;
        }

        TopicResponseTo response = new TopicResponseTo();
        response.setId(entity.getId());
        response.setEditorId(entity.getEditor().getId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());

        // Собираем ID тегов
        if (entity.getTags() != null) {
            Set<Long> tagIds = new HashSet<>();
            for (Tag tag : entity.getTags()) {
                tagIds.add(tag.getId());
            }
            response.setTagIds(tagIds);
        }

        // Сообщения больше не возвращаем - они в отдельном модуле

        return response;
    }
}

