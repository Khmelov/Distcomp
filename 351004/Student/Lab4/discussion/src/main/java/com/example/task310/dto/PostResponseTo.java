package com.example.task310.dto;

import com.example.task310.enums.PostState;

public record PostResponseTo(Long id, Long issueId, String content, PostState state) {}