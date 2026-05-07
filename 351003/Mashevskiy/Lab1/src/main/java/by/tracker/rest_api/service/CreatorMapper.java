package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.CreatorRequestTo;
import by.tracker.rest_api.dto.CreatorResponseTo;
import by.tracker.rest_api.model.Creator;
import org.springframework.stereotype.Component;

@Component
public class CreatorMapper {

    public Creator toEntity(CreatorRequestTo request) {
        Creator creator = new Creator();
        creator.setLogin(request.getLogin());
        creator.setPassword(request.getPassword());
        creator.setFirstName(request.getFirstName());
        creator.setLastName(request.getLastName());
        return creator;
    }

    public CreatorResponseTo toResponse(Creator entity) {
        CreatorResponseTo response = new CreatorResponseTo();
        response.setId(entity.getId());
        response.setLogin(entity.getLogin());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        return response;
    }
}