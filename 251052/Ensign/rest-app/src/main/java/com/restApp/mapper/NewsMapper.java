package com.restApp.mapper;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;
import com.restApp.model.News;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class NewsMapper {

    private final CommentMapper commentMapper;
    private final MarkMapper markMapper;

    public NewsMapper(CommentMapper commentMapper, MarkMapper markMapper) {
        this.commentMapper = commentMapper;
        this.markMapper = markMapper;
    }

    public News toEntity(NewsRequestTo request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setAuthorId(request.getAuthorId());
        return news;
    }

    public NewsResponseTo toResponse(News entity) {
        NewsResponseTo response = new NewsResponseTo();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setTimestamp(entity.getTimestamp());
        response.setAuthorId(entity.getAuthorId());

        response.setComments(entity.getComments() != null
                ? entity.getComments().stream().map(commentMapper::toResponse).collect(Collectors.toList())
                : Collections.emptyList());

        response.setMarks(entity.getMarks() != null
                ? entity.getMarks().stream().map(markMapper::toResponse).collect(Collectors.toList())
                : Collections.emptyList());

        return response;
    }

    public void updateEntity(News entity, NewsRequestTo request) {
        if (request.getTitle() != null)
            entity.setTitle(request.getTitle());
        if (request.getContent() != null)
            entity.setContent(request.getContent());
    }
}
