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
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TweetMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "marks", source = "marks", qualifiedByName = "marksToStrings")
    TweetResponseTo toResponseTo(Tweet tweet);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "marks", source = "marks", qualifiedByName = "stringsToMarks")
    Tweet toEntity(TweetRequestTo requestTo);

    @Named("marksToStrings")
    default List<String> marksToStrings(Set<Mark> marks) {
        if (marks == null) {
            return null;
        }
        return marks.stream()
                .map(Mark::getName)
                .collect(Collectors.toList());
    }

    @Named("stringsToMarks")
    default Set<Mark> stringsToMarks(List<String> marks) {
        if (marks == null) {
            return null;
        }
        return marks.stream()
                .map(name -> {
                    Mark mark = new Mark();
                    mark.setName(name);
                    return mark;
                })
                .collect(Collectors.toSet());
    }
}