package by.bsuir.rest.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageQuery(int page, int size, String sortBy, boolean ascending) {

    public static PageQuery all() {
        return new PageQuery(0, 0, "id", true);
    }

    public static PageQuery page(int page, int size, String sortBy, boolean ascending) {
        return new PageQuery(page, size, sortBy, ascending);
    }

    public Pageable toPageable() {
        String property = sortBy == null || sortBy.isBlank() ? "id" : sortBy;
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, property);
        if (size <= 0) {
            return Pageable.unpaged(sort);
        }
        return PageRequest.of(Math.max(page, 0), size, sort);
    }
}
