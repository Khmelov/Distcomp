package by.bsuir.task330.publisher.mapper;

import by.bsuir.task330.publisher.domain.Creator;
import by.bsuir.task330.publisher.dto.request.CreatorRequestTo;
import by.bsuir.task330.publisher.dto.response.CreatorResponseTo;
import org.springframework.stereotype.Component;

@Component
public class CreatorMapper {

    public Creator toEntity(CreatorRequestTo dto) {
        Creator c = new Creator();
        c.setId(dto.getId());
        c.setLogin(dto.getLogin());
        c.setPassword(dto.getPassword());
        c.setFirstname(dto.getFirstname());
        c.setLastname(dto.getLastname());
        return c;
    }

    public CreatorResponseTo toResponse(Creator entity) {
        CreatorResponseTo dto = new CreatorResponseTo();
        dto.setId(entity.getId());
        dto.setLogin(entity.getLogin());
        dto.setFirstname(entity.getFirstname());
        dto.setLastname(entity.getLastname());
        return dto;
    }
}