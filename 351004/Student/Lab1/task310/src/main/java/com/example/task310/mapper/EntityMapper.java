package com.example.task310.mapper;

import com.example.task310.dto.*;
import com.example.task310.entity.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // 1. Writer
    public Writer toEntity(WriterRequestTo request) {
        if (request == null) return null;
        return new Writer(request.getId(), request.getLogin(), request.getPassword(), request.getFirstname(), request.getLastname());
    }

    public WriterResponseTo toResponse(Writer entity) {
        if (entity == null) return null;
        return new WriterResponseTo(entity.getId(), entity.getLogin(), entity.getFirstname(), entity.getLastname());
    }

    public List<WriterResponseTo> toWriterResponseList(List<Writer> entities) {
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 2. Issue
    public Issue toEntity(IssueRequestTo request) {
        if (request == null) return null;
        return new Issue(request.getId(), request.getWriterId(), request.getTitle(), request.getContent(), null, null);
    }

    public IssueResponseTo toResponse(Issue entity) {
        if (entity == null) return null;
        return new IssueResponseTo(entity.getId(), entity.getWriterId(), entity.getTitle(), entity.getContent(), entity.getCreated(), entity.getModified());
    }

    public List<IssueResponseTo> toIssueResponseList(List<Issue> entities) {
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 3. Marker
    public Marker toEntity(MarkerRequestTo request) {
        if (request == null) return null;
        return new Marker(request.getId(), request.getName());
    }

    public MarkerResponseTo toResponse(Marker entity) {
        if (entity == null) return null;
        return new MarkerResponseTo(entity.getId(), entity.getName());
    }

    public List<MarkerResponseTo> toMarkerResponseList(List<Marker> entities) {
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 4. Post
    public Post toEntity(PostRequestTo request) {
        if (request == null) return null;
        return new Post(request.getId(), request.getIssueId(), request.getContent());
    }

    public PostResponseTo toResponse(Post entity) {
        if (entity == null) return null;
        return new PostResponseTo(entity.getId(), entity.getIssueId(), entity.getContent());
    }

    public List<PostResponseTo> toPostResponseList(List<Post> entities) {
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}