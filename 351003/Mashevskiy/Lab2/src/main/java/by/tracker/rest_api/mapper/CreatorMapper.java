package by.tracker.rest_api.mapper;

import by.tracker.rest_api.dto.CreatorRequestDto;
import by.tracker.rest_api.dto.CreatorResponseDto;
import by.tracker.rest_api.entity.Creator;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Component
public class CreatorMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public Creator toEntity(CreatorRequestDto dto) {
        if (dto == null) return null;
        Creator creator = new Creator();
        creator.setId(dto.getId());
        creator.setLogin(dto.getLogin());
        creator.setPassword(dto.getPassword());
        creator.setFirstname(dto.getFirstname());
        creator.setLastname(dto.getLastname());
        return creator;
    }

    public CreatorResponseDto toResponseDto(Creator entity) {
        if (entity == null) return null;
        CreatorResponseDto dto = new CreatorResponseDto();
        dto.setId(entity.getId());
        dto.setLogin(entity.getLogin());
        dto.setFirstname(entity.getFirstname());
        dto.setLastname(entity.getLastname());
        if (entity.getCreated() != null) {
            dto.setCreated(entity.getCreated().format(formatter));
        }
        if (entity.getModified() != null) {
            dto.setModified(entity.getModified().format(formatter));
        }
        return dto;
    }

    public void updateEntity(CreatorRequestDto dto, Creator entity) {
        if (dto.getLogin() != null) entity.setLogin(dto.getLogin());
        if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
        if (dto.getFirstname() != null) entity.setFirstname(dto.getFirstname());
        if (dto.getLastname() != null) entity.setLastname(dto.getLastname());
    }
}