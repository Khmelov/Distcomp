package com.blog.util;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.request.TagRequestTo;
import com.blog.dto.request.TopicRequestTo;

import java.util.HashSet;
import java.util.Set;

public class TestData {

    public static EditorRequestTo createValidEditorRequest() {
        EditorRequestTo editor = new EditorRequestTo();
        editor.setLogin("test.editor@example.com");
        editor.setPassword("password123");
        editor.setFirstname("Тестовый");
        editor.setLastname("Редактор");
        return editor;
    }

    public static TagRequestTo createValidTagRequest() {
        return new TagRequestTo("ТестовыйТег");
    }

    public static TopicRequestTo createValidTopicRequest(Long editorId, Long... tagIds) {
        TopicRequestTo topic = new TopicRequestTo();
        topic.setEditorId(editorId);
        topic.setTitle("Тестовая тема");
        topic.setContent("Содержание тестовой темы длиннее 4 символов");

        if (tagIds.length > 0) {
            Set<Long> tagIdsSet = new HashSet<>();
            for (Long tagId : tagIds) {
                tagIdsSet.add(tagId);
            }
            topic.setTagIds(tagIdsSet);
        }

        return topic;
    }
}