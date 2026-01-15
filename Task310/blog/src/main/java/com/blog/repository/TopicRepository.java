package com.blog.repository;

import com.blog.model.Topic;
import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Long> {
    List<Topic> findByEditorId(Long editorId);
    List<Topic> findByTagIdsContaining(Long tagId);
}