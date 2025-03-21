package org.ex.distributed_computing.dto.response;

import java.time.LocalDateTime;

public record NewsResponseDTO(Long id, Long authorId, String title, String content,
                              LocalDateTime createdDateTime, LocalDateTime updatedDateTime) {}
