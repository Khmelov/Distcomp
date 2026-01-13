package com.publick.service.mapper;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.entity.Author;
import com.publick.entity.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public Issue toEntity(IssueRequestTo dto, Author author) {
        if (dto == null) {
            return null;
        }
        Issue issue = new Issue(author, dto.getTitle(), dto.getContent());
        return issue;
    }

    public IssueResponseTo toResponse(Issue entity) {
        if (entity == null) {
            return null;
        }
        IssueResponseTo response = new IssueResponseTo();
        response.setId(entity.getId());
        response.setAuthorId(entity.getAuthor().getId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());
        return response;
    }

    public void updateEntityFromDto(IssueRequestTo dto, Issue entity, Author author) {
        if (dto != null && entity != null) {
            entity.setAuthor(author);
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());
        }
    }
}