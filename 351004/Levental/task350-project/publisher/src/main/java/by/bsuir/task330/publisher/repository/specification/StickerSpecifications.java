package by.bsuir.task330.publisher.repository.specification;

import by.bsuir.task330.publisher.domain.Sticker;
import org.springframework.data.jpa.domain.Specification;

public final class StickerSpecifications {
    private StickerSpecifications() {}

    public static Specification<Sticker> byFilter(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
        };
    }
}
