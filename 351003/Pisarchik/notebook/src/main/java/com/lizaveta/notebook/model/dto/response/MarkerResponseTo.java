package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Response DTO for Marker entity.
 */
@JsonRootName("marker")
public record MarkerResponseTo(
        Long id,
        String name) {
}
