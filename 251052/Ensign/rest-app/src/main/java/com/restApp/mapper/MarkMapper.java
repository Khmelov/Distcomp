package com.restApp.mapper;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;
import com.restApp.model.Mark;
import org.springframework.stereotype.Component;

@Component
public class MarkMapper {

    public Mark toEntity(MarkRequestTo request) {
        Mark mark = new Mark();
        mark.setName(request.getName());
        return mark;
    }

    public MarkResponseTo toResponse(Mark entity) {
        MarkResponseTo response = new MarkResponseTo();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }

    public void updateEntity(Mark entity, MarkRequestTo request) {
        if (request.getName() != null)
            entity.setName(request.getName());
    }
}
