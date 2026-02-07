package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.Author;
import com.example.demo.model.Issue;
import com.example.demo.model.Mark;
import com.example.demo.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    Author toEntity(AuthorRequestTo dto);
    AuthorResponseTo toResponse(Author author);
    void updateEntity(AuthorRequestTo dto, @MappingTarget Author entity);

    Issue toIssueEntity(IssueRequestTo dto);
    IssueResponseTo toIssueResponse(Issue issue);
    void updateEntity(IssueRequestTo dto, @MappingTarget Issue entity);

    Mark toMarkEntity(MarkRequestTo dto);
    MarkResponseTo toMarkResponse(Mark mark);
    void updateEntity(MarkRequestTo dto, @MappingTarget Mark entity);

    Message toMessageEntity(MessageRequestTo dto);
    MessageResponseTo toMessageResponse(Message entity);
    void updateEntity(MessageRequestTo dto, @MappingTarget Message entity);

}
