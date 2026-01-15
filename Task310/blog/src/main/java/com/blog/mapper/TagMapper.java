package com.blog.mapper;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(TagRequestTo request) {
        if (request == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        // id устанавливается репозиторием
        return tag;
    }

    public TagResponseTo toResponse(Tag entity) {
        if (entity == null) {
            return null;
        }

        TagResponseTo response = new TagResponseTo();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
}