package com.github.Lexya06.startrestapp.discussion.impl.model.repository.realization;

import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.NoticeKey;
import com.github.Lexya06.startrestapp.discussion.impl.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends MyCrudRepositoryImpl<Notice, NoticeKey> {

    @Query("SELECT * FROM tbl_notice WHERE country = ?0 ALLOW FILTERING")
    Slice<Notice> findByCountry(String country, Pageable pageable);

    @Query("SELECT * FROM tbl_notice WHERE country = ?0 AND article_id = ?1")
    Slice<Notice> findByCountryAndArticleId(String country, Long articleId, Pageable pageable);


    @Query("SELECT * FROM tbl_notice WHERE article_id = ?0 ALLOW FILTERING")
    Slice<Notice> findByArticleId(Long articleId, Pageable pageable);


    @Query("SELECT * FROM tbl_notice WHERE id = ?0 ALLOW FILTERING")
    Optional<Notice> findByIdId(Long id);

}
