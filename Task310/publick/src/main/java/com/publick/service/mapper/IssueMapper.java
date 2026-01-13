package com.publick.service.mapper;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.entity.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public Issue toEntity(IssueRequestTo dto) {
        if (dto == null) {
            return null;
        }
        Issue issue = new Issue();
        issue.setAuthorId(dto.getAuthorId());
        issue.setTitle(dto.getTitle());
        issue.setContent(dto.getContent());
        return issue;
    }

    public IssueResponseTo toResponse(Issue entity) {
        if (entity == null) {
            return null;
        }
        IssueResponseTo response = new IssueResponseTo();
        response.setId(entity.getId());
        response.setAuthorId(entity.getAuthorId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());
        return response;
    }

    public void updateEntityFromDto(IssueRequestTo dto, Issue entity) {
        if (dto != null && entity != null) {
            entity.setAuthorId(dto.getAuthorId());
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());
        }
    }
}