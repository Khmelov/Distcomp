package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import by.bsuir.task310.model.Reaction;
import org.springframework.stereotype.Component;

@Component
public class ReactionMapper {

    public Reaction toEntity(ReactionRequestTo requestTo) {
        Reaction reaction = new Reaction();
        reaction.setId(requestTo.getId());
        reaction.setTopicId(requestTo.getTopicId());
        reaction.setContent(requestTo.getContent());
        return reaction;
    }

    public ReactionResponseTo toResponseTo(Reaction reaction) {
        ReactionResponseTo responseTo = new ReactionResponseTo();
        responseTo.setId(reaction.getId());
        responseTo.setTopicId(reaction.getTopicId());
        responseTo.setContent(reaction.getContent());
        return responseTo;
    }
}