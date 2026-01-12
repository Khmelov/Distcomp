package com.task.discussion.repository;

import com.task.discussion.model.Notice;
import com.task.discussion.model.NoticePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends CassandraRepository<Notice, NoticePrimaryKey> {

    List<Notice> findByCountry(String country);

    List<Notice> findByCountryAndTweetId(String country, Long tweetId);

    Optional<Notice> findByCountryAndTweetIdAndId(String country, Long tweetId, Long id);
}