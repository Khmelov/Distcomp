package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Notice;

import java.util.List;

/**
 * Repository interface for Notice entity with story-specific queries.
 */
public interface NoticeRepository extends CrudRepository<Notice, Long> {

    List<Notice> findByStoryId(Long storyId);
}
