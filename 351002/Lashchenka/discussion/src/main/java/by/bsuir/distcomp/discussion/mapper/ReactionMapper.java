package by.bsuir.distcomp.discussion.mapper;

import by.bsuir.distcomp.discussion.cassandra.ReactionRow;
import by.bsuir.distcomp.discussion.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.discussion.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.discussion.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    Reaction toEntity(ReactionRequestTo dto);

    Reaction fromRow(ReactionRow row);

    ReactionRow toRow(Reaction entity);

    ReactionResponseTo toResponseDto(Reaction entity);

    void updateEntityFromDto(ReactionRequestTo dto, @MappingTarget Reaction entity);
}
