package com.rest.restapp.dto.response;

public record IssueResponseToDto(
        Long id,
        Long userId,
        String title,
        String content,
        String created,
        String modified
){
}
