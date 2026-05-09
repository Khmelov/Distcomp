package by.bsuir.distcomp.core.mapper;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.core.domain.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    @Mapping(target = "tweet", ignore = true)
    Reaction toEntity(ReactionRequestTo dto);

    @Mapping(target = "tweetId", source = "tweet.id")
    ReactionResponseTo toResponseDto(Reaction entity);
}