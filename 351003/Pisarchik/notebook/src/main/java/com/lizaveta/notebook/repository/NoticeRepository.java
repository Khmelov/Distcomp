package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Notice;

import java.util.List;

public interface NoticeRepository extends CrudRepository<Notice, Long> {

    List<Notice> findByStoryId(Long storyId);
}
