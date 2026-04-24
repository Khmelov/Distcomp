package by.shaminko.distcomp.dto;

import by.shaminko.distcomp.entities.Tweet;
import java.sql.Timestamp;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T13:24:47+0300",
    comments = "version: 1.6.1, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class TweetMapperImpl implements TweetMapper {

    @Override
    public TweetResponseTo out(Tweet tweet) {
        if ( tweet == null ) {
            return null;
        }

        TweetResponseTo tweetResponseTo = new TweetResponseTo();

        tweetResponseTo.setId( tweet.getId() );
        tweetResponseTo.setCreatorId( tweet.getCreatorId() );
        tweetResponseTo.setTitle( tweet.getTitle() );
        tweetResponseTo.setContent( tweet.getContent() );
        tweetResponseTo.setCreated( tweet.getCreated() );
        tweetResponseTo.setModified( tweet.getModified() );

        fillMarkers( tweet, tweetResponseTo );

        return tweetResponseTo;
    }

    @Override
    public Tweet in(TweetRequestTo tweet) {
        if ( tweet == null ) {
            return null;
        }

        Tweet.TweetBuilder tweet1 = Tweet.builder();

        tweet1.id( tweet.getId() );
        tweet1.creatorId( tweet.getCreatorId() );
        tweet1.title( tweet.getTitle() );
        tweet1.content( tweet.getContent() );

        tweet1.created( Timestamp.from(Instant.now()) );
        tweet1.modified( Timestamp.from(Instant.now()) );

        return tweet1.build();
    }
}
