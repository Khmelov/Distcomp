package com.distcomp.errorhandling.model;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponse (
    int status,
    String message,
    Instant timestamp,
    List<ValidationError> details
){}
