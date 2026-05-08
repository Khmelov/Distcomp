package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.model.Author;
import com.example.demo.model.Issue;
import com.example.demo.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    // AUTHOR
    Author toEntity(AuthorRequestTo dto);
    AuthorResponseTo toAuthorResponse(Author author);
    List<AuthorResponseTo> toAuthorResponseList(List<Author> authors);
    void updateAuthor(AuthorRequestTo dto, @MappingTarget Author entity);

    // ISSUE
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "marks", ignore = true)
    Issue toEntity(IssueRequestTo dto);
    @Mapping(source = "author.id", target = "authorId")
    IssueResponseTo toIssueResponse(Issue issue);
    List<IssueResponseTo> toIssueResponseList(List<Issue> issues);
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "marks", ignore = true)
    void updateIssue(IssueRequestTo dto, @MappingTarget Issue entity);

    // MARK
    Mark toEntity(MarkRequestTo dto);
    MarkResponseTo toMarkResponse(Mark mark);
    List<MarkResponseTo> toMarkResponseList(List<Mark> marks);
    void updateMark(MarkRequestTo dto, @MappingTarget Mark entity);
}
