package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.Tweet;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {Timestamp.class, Instant.class})
public interface TweetMapper {
    @Mapping(target = "stickers", ignore = true)
    TweetResponseTo out(Tweet tweet);
    @Mapping(target = "created", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "modified", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "stickers", ignore = true)
    Tweet in(TweetRequestTo tweet);

    @AfterMapping
    default void fillStickers(Tweet tweet, @MappingTarget TweetResponseTo response) {
        List<String> stickers = tweet.getStickers() == null
                ? List.of()
                : tweet.getStickers().stream().map(sticker -> sticker.getName()).sorted().toList();
        response.setStickers(stickers);
    }
}
