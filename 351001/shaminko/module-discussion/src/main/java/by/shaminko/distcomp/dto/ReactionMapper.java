package by.shaminko.distcomp.dto;

import by.shaminko.distcomp.entities.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    Reaction in(ReactionRequestTo request);

    ReactionResponseTo out(Reaction entity);
}