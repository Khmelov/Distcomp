package by.bsuir.task330.publisher.repository.specification;

import by.bsuir.task330.publisher.domain.Article;
import org.springframework.data.jpa.domain.Specification;

public final class ArticleSpecifications {
    private ArticleSpecifications() {}

    public static Specification<Article> byFilter(String search, Long creatorId) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("content")), pattern)
                ));
            }
            if (creatorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("creator").get("id"), creatorId));
            }
            return predicate;
        };
    }
}
