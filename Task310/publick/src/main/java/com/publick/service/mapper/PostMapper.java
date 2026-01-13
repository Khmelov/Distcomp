package com.publick.service.mapper;

import com.publick.dto.PostRequestTo;
import com.publick.dto.PostResponseTo;
import com.publick.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostRequestTo dto) {
        if (dto == null) {
            return null;
        }
        Post post = new Post();
        post.setIssueId(dto.getIssueId());
        post.setContent(dto.getContent());
        return post;
    }

    public PostResponseTo toResponse(Post entity) {
        if (entity == null) {
            return null;
        }
        PostResponseTo response = new PostResponseTo();
        response.setId(entity.getId());
        response.setIssueId(entity.getIssueId());
        response.setContent(entity.getContent());
        return response;
    }

    public void updateEntityFromDto(PostRequestTo dto, Post entity) {
        if (dto != null && entity != null) {
            entity.setContent(dto.getContent());
            entity.setIssueId(dto.getIssueId());
        }
    }
}