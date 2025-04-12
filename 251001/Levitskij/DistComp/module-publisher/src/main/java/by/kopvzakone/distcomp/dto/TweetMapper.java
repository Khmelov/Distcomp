package by.kopvzakone.distcomp.dto;

import by.kopvzakone.distcomp.entities.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {Timestamp.class, Instant.class})
public interface TweetMapper {
    TweetResponseTo out(Tweet tweet);
    @Mapping(target = "created", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "modified", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "tags", ignore = true)
    Tweet in(TweetRequestTo tweet);
}
