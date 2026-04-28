package by.shaminko.distcomp.dto;

import by.shaminko.distcomp.entities.Tweet;
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
    TweetResponseTo out(Tweet tweet);
    @Mapping(target = "created", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "modified", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "tags", ignore = true)
    Tweet in(TweetRequestTo tweet);

    @AfterMapping
    default void fillMarkers(Tweet tweet, @MappingTarget TweetResponseTo response) {
        List<String> markers = tweet.getTags() == null
                ? List.of()
                : tweet.getTags().stream().map(tag -> tag.getName()).sorted().toList();
        response.setMarkers(markers);
    }
}
