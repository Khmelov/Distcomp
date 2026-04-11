package by.bsuir.task330.discussion.mapper;

import by.bsuir.task330.discussion.dto.request.ReactionRequestTo;
import by.bsuir.task330.discussion.dto.response.ReactionResponseTo;
import by.bsuir.task330.discussion.entity.Reaction;

public final class ReactionMapper {
    private ReactionMapper() {
    }

    public static Reaction toEntity(Long id, ReactionRequestTo request) {
        return new Reaction(id, request.tweetId(), request.content().trim());
    }

    public static void updateEntity(Reaction reaction, ReactionRequestTo request) {
        reaction.setTweetId(request.tweetId());
        reaction.setContent(request.content().trim());
    }

    public static ReactionResponseTo toResponse(Reaction reaction) {
        return new ReactionResponseTo(reaction.getId(), reaction.getTweetId(), reaction.getContent());
    }
}
