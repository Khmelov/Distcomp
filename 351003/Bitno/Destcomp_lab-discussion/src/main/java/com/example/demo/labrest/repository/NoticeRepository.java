package com.example.demo.labrest.repository;

import com.example.demo.labrest.model.NoticeEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends CassandraRepository<NoticeEntity, Long> {

    @Query("SELECT * FROM tbl_notices WHERE topic_id = ?0 ALLOW FILTERING")
    List<NoticeEntity> findByTopicId(Long topicId);

    Optional<NoticeEntity> findById(Long id);
}