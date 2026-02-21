package com.lizaveta.notebook.model.dto.response;

import java.util.List;

public record PageResponseTo<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        int number) {
}
