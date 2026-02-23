package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Response DTO for Writer entity.
 */
@JsonRootName("writer")
public record WriterResponseTo(
        Long id,
        String login,
        String firstname,
        String lastname) {
}
