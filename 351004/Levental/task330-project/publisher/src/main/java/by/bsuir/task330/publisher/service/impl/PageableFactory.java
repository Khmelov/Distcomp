package by.bsuir.task330.publisher.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableFactory {
    public Pageable create(Integer page, Integer size, String sortExpression, String defaultSortField) {
        int normalizedPage = page == null || page < 0 ? 0 : page;
        int normalizedSize = size == null || size <= 0 ? 10 : size;
        String sortValue = (sortExpression == null || sortExpression.isBlank()) ? defaultSortField + ",asc" : sortExpression;

        String[] parts = sortValue.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(normalizedPage, normalizedSize, Sort.by(direction, field));
    }
}
