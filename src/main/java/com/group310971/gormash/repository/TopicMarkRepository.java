package com.group310971.gormash.repository;

import com.group310971.gormash.model.Topic;
import com.group310971.gormash.model.TopicMark;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicMarkRepository extends JpaRepository<TopicMark, Long> {
    List<TopicMark> findByTopic(@NonNull Topic topicId);
}
