package com.example.task310.dto;
import com.example.task310.enums.PostState;
import java.io.Serializable;

public record PostResponseTo(Long id, Long issueId, String content, PostState state) implements Serializable {}