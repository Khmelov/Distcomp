package by.bsuir.task340.discussion.repository;

import by.bsuir.task340.discussion.domain.Notice;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends CassandraRepository<Notice, Long> {
    List<Notice> findByArticleId(Long articleId);
}
