package by.bsuir.task340.discussion.mapper;

import by.bsuir.task340.discussion.dto.ReactionState;
import by.bsuir.task340.discussion.dto.request.ReactionRequestTo;
import by.bsuir.task340.discussion.dto.response.ReactionResponseTo;
import by.bsuir.task340.discussion.entity.Reaction;

public final class ReactionMapper {
    private ReactionMapper() {
    }

    public static Reaction toEntity(Long id, ReactionRequestTo request, ReactionState state) {
        return new Reaction(id, request.tweetId(), request.content().trim(), state.name());
    }

    public static void updateEntity(Reaction reaction, ReactionRequestTo request, ReactionState state) {
        reaction.setTweetId(request.tweetId());
        reaction.setContent(request.content().trim());
        reaction.setState(state.name());
    }

    public static ReactionResponseTo toResponse(Reaction reaction) {
        ReactionState state = reaction.getState() == null || reaction.getState().isBlank()
                ? ReactionState.PENDING
                : ReactionState.valueOf(reaction.getState());
        return new ReactionResponseTo(
                reaction.getId(),
                reaction.getTweetId(),
                reaction.getContent(),
                state
        );
    }
}
