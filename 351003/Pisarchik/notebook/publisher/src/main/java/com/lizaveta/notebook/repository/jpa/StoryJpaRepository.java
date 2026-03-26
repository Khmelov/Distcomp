package com.lizaveta.notebook.repository.jpa;

import com.lizaveta.notebook.repository.entity.StoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoryJpaRepository extends JpaRepository<StoryEntity, Long> {

    @Query("SELECT s FROM StoryEntity s JOIN s.markers m WHERE m.id = :markerId")
    List<StoryEntity> findByMarkerId(@Param("markerId") Long markerId);

    @Query("SELECT s FROM StoryEntity s JOIN s.markers m WHERE m.id = :markerId")
    Page<StoryEntity> findByMarkerId(@Param("markerId") Long markerId, Pageable pageable);

    boolean existsByWriterIdAndTitle(Long writerId, String title);

    boolean existsByWriterIdAndTitleAndIdNot(Long writerId, String title, Long id);
}
