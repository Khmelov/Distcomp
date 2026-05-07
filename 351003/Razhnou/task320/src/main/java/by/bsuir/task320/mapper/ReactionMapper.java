package by.bsuir.task320.mapper;

import by.bsuir.task320.dto.response.ReactionResponseTo;
import by.bsuir.task320.entity.Reaction;
import by.bsuir.task320.entity.Tweet;

public final class ReactionMapper {
    private ReactionMapper() {
    }

    public static Reaction toEntity(Tweet tweet, String content) {
        Reaction reaction = new Reaction();
        reaction.setTweet(tweet);
        reaction.setContent(content.trim());
        return reaction;
    }

    public static void updateEntity(Reaction reaction, Tweet tweet, String content) {
        reaction.setTweet(tweet);
        reaction.setContent(content.trim());
    }

    public static ReactionResponseTo toResponse(Reaction reaction) {
        return new ReactionResponseTo(
                reaction.getId(),
                reaction.getTweet().getId(),
                reaction.getContent()
        );
    }
}
