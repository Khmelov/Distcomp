package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("marker")
public record MarkerResponseTo(
        Long id,
        String name) {
}
