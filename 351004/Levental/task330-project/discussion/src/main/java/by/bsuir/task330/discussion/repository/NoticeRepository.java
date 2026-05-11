package by.bsuir.task330.discussion.repository;

import by.bsuir.task330.discussion.entity.NoticeEntity;
import by.bsuir.task330.discussion.entity.NoticeKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface NoticeRepository extends CassandraRepository<NoticeEntity, NoticeKey> {
    List<NoticeEntity> findByKeyArticleId(Long articleId);

    @Query("SELECT max(id) FROM tbl_notice WHERE article_id = ?0")
    Long findMaxIdByArticleId(Long articleId);
}
