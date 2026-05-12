package by.shaminko.distcomp.dto;

import by.shaminko.distcomp.entities.Reaction;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T13:24:47+0300",
    comments = "version: 1.6.1, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class ReactionMapperImpl implements ReactionMapper {

    @Override
    public ReactionResponseTo out(Reaction editor) {
        if ( editor == null ) {
            return null;
        }

        ReactionResponseTo reactionResponseTo = new ReactionResponseTo();

        reactionResponseTo.setId( editor.getId() );
        reactionResponseTo.setArticleId( editor.getArticleId() );
        reactionResponseTo.setContent( editor.getContent() );
        reactionResponseTo.setState( editor.getState() );

        return reactionResponseTo;
    }

    @Override
    public Reaction in(ReactionRequestTo editor) {
        if ( editor == null ) {
            return null;
        }

        Reaction.ReactionBuilder reaction = Reaction.builder();

        reaction.id( editor.getId() );
        reaction.articleId( editor.getArticleId() );
        reaction.content( editor.getContent() );
        reaction.state( editor.getState() );

        return reaction.build();
    }
}
