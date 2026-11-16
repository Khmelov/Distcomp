package by.bsuir.rest.repository;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalElements) {

    public static <T> PageResult<T> of(Page<T> page) {
        boolean paged = page.getPageable().isPaged();
        int pageNumber = paged ? page.getNumber() : 0;
        int pageSize = paged ? page.getSize() : page.getNumberOfElements();
        return new PageResult<>(List.copyOf(page.getContent()), pageNumber, pageSize, page.getTotalElements());
    }
}
