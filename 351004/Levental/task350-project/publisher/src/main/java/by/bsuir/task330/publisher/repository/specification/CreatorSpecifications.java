package by.bsuir.task330.publisher.repository.specification;

import by.bsuir.task330.publisher.domain.Creator;
import org.springframework.data.jpa.domain.Specification;

public final class CreatorSpecifications {
    private CreatorSpecifications() {}

    public static Specification<Creator> byFilter(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("login")), pattern),
                    cb.like(cb.lower(root.get("firstname")), pattern),
                    cb.like(cb.lower(root.get("lastname")), pattern)
            );
        };
    }
}
