package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import org.springframework.stereotype.Component;

@Component
public class CreatorMapper {

    public Creator toEntity(CreatorRequestTo request) {
        if (request == null) {
            return null;
        }
        return new Creator(
                request.getId(),
                request.getLogin(),
                request.getPassword(),
                request.getFirstname(),
                request.getLastname()
        );
    }

    public CreatorResponseTo toResponse(Creator entity) {
        if (entity == null) {
            return null;
        }
        return new CreatorResponseTo(
                entity.getId(),
                entity.getLogin(),
                entity.getFirstname(),
                entity.getLastname()
        );
    }
}
