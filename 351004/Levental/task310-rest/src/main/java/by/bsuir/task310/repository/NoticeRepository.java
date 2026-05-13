package by.bsuir.task310.repository;

import by.bsuir.task310.domain.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoticeRepository extends InMemoryCrudRepository<Notice> {
    public List<Notice> findByArticleId(Long articleId) {
        return storage.values().stream()
                .filter(n -> n.getArticleId().equals(articleId))
                .toList();
    }
}
