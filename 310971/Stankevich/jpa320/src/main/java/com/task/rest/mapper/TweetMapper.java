package com.task.rest.mapper;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.model.Mark;
import com.task.rest.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TweetMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "marks", source = "marks", qualifiedByName = "marksToNames")
    TweetResponseTo toResponseTo(Tweet tweet);

    @Named("marksToNames")
    default List<String> marksToNames(List<Mark> marks) {
        if (marks == null) {
            return null;
        }
        return marks.stream()
                .map(Mark::getName)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "notices", ignore = true)
    Tweet toEntity(TweetRequestTo requestTo);
}
