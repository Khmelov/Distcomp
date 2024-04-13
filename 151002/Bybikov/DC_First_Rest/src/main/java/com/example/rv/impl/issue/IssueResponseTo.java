package com.example.rv.impl.issue;

public record IssueResponseTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        String created,
        String modified
){

}
