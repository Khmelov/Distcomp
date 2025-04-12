package by.kopvzakone.distcomp.dto;

import by.kopvzakone.distcomp.entities.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReactionMapper {
    @Mapping(target = "id", source = "id.id")
    ReactionResponseTo out(Reaction editor);

    @Mapping(target = "id.id", source = "id")
    @Mapping(target = "id.country", constant = "by")
    Reaction in(ReactionRequestTo editor);
}
