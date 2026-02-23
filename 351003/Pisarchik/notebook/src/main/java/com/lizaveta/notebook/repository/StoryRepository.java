package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Story;

import java.util.List;

/**
 * Repository interface for Story entity with marker-specific queries.
 */
public interface StoryRepository extends CrudRepository<Story, Long> {

    List<Story> findByMarkerId(Long markerId);
}
