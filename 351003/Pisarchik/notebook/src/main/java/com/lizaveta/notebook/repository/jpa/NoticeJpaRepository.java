package com.lizaveta.notebook.repository.jpa;

import com.lizaveta.notebook.repository.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {

    List<NoticeEntity> findByStoryId(Long storyId);
}
