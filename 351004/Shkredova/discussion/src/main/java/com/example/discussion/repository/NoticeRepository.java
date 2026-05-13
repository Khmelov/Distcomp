package com.example.discussion.repository;

import com.example.discussion.model.Notice;
import com.example.discussion.model.NoticeKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends CassandraRepository<Notice, NoticeKey> {
    @Query("SELECT * FROM tbl_notice WHERE country = :country AND news_id = :newsId")
    List<Notice> findByNewsId(@Param("country") String country, @Param("newsId") Long newsId);

    @Query("SELECT * FROM tbl_notice WHERE country = :country AND news_id = :newsId AND id = :id")
    Optional<Notice> findByNewsIdAndId(@Param("country") String country,
                                       @Param("newsId") Long newsId,
                                       @Param("id") Long id);
}