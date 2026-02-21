package com.distcomp.errorhandling.model;

import lombok.Builder;

@Builder
public record ValidationError(String field, String message) { }