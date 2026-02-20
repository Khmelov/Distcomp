package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    Reaction toEntity(ReactionRequestTo dto);
    ReactionResponseTo toResponseDto(Reaction entity);
    void updateEntityFromDto(ReactionRequestTo dto, @MappingTarget Reaction entity);
}
