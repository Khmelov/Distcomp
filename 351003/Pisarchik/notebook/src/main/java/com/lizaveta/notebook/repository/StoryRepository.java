package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Story;

import java.util.List;

public interface StoryRepository extends CrudRepository<Story, Long> {

    List<Story> findByMarkerId(Long markerId);

    boolean existsByWriterIdAndTitle(Long writerId, String title);

    boolean existsByWriterIdAndTitleAndIdNot(Long writerId, String title, Long excludeId);
}
