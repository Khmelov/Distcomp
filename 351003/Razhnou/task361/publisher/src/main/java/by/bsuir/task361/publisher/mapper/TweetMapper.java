package by.bsuir.task361.publisher.mapper;

import by.bsuir.task361.publisher.dto.response.TweetResponseTo;
import by.bsuir.task361.publisher.entity.Tag;
import by.bsuir.task361.publisher.entity.Tweet;
import by.bsuir.task361.publisher.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

public final class TweetMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private TweetMapper() {
    }

    public static Tweet toEntity(User user, String title, String content, LocalDateTime created, LocalDateTime modified, Set<Tag> tags) {
        Tweet tweet = new Tweet();
        tweet.setUser(user);
        tweet.setTitle(title.trim());
        tweet.setContent(content.trim());
        tweet.setCreated(created);
        tweet.setModified(modified);
        tweet.setTags(tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags));
        return tweet;
    }

    public static void updateEntity(Tweet tweet, User user, String title, String content, LocalDateTime modified, Set<Tag> tags) {
        tweet.setUser(user);
        tweet.setTitle(title.trim());
        tweet.setContent(content.trim());
        tweet.setModified(modified);
        tweet.setTags(tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags));
    }

    public static TweetResponseTo toResponse(Tweet tweet) {
        return new TweetResponseTo(
                tweet.getId(),
                tweet.getUser().getId(),
                tweet.getTitle(),
                tweet.getContent(),
                tweet.getCreated().format(FORMATTER),
                tweet.getModified().format(FORMATTER)
        );
    }
}
