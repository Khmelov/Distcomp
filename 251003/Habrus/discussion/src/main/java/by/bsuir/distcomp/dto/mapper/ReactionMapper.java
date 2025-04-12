package by.bsuir.distcomp.dto.mapper;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.entity.ReactionKey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ReactionMapper {

    public static Reaction toEntity(String country, ReactionRequestTo dto) {
        ReactionKey key = new ReactionKey(country, dto.getId());
        return new Reaction(key, dto.getArticleId(), dto.getContent());
    }

    public static ReactionResponseTo toDto(Reaction reaction) {
        ReactionResponseTo dto = new ReactionResponseTo();
        dto.setArticleId(reaction.getArticleId());
        dto.setId(reaction.getKey().getId());
        dto.setContent(reaction.getContent());
        return dto;
    }

}
