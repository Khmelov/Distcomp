package com.task.rest.util;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.model.Mark;
import com.task.rest.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TweetMapper {
    @Mapping(target = "markIds", expression = "java(mapMarksToIds(tweet.getMarks()))")
    TweetResponseTo toResponse(Tweet tweet);

    Tweet toEntity(TweetRequestTo requestTo);
    void updateEntityFromDto(TweetRequestTo requestTo, @MappingTarget Tweet tweet);

    default List<Long> mapMarksToIds(List<Mark> marks) {
        if (marks == null) return java.util.Collections.emptyList();
        return marks.stream().map(Mark::getId).toList();
    }
}