package com.example.demo.labrest.repository;

import com.example.demo.labrest.model.Notice;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticeRepository extends BaseRepository<Notice, Long> {
    List<Notice> findByTopic_Id(Long topicId);
}