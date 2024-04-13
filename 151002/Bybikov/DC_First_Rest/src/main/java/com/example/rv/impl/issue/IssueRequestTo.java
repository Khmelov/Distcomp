package com.example.rv.impl.issue;

public record IssueRequestTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        String created,
        String modified
){

}
