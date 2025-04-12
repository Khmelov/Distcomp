package com.example.rest.mapper;

import com.example.rest.dto.topic.TopicRequestTo;
import com.example.rest.dto.topic.TopicResponseTo;
import com.example.rest.entity.Tag;
import com.example.rest.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @Mapping(target = "creator.id", source = "creatorId")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapStringsToTags")
    Topic toEntity(TopicRequestTo topicRequestTo);

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTagsToStrings")
    TopicResponseTo toResponse(Topic topic);

    @Named("mapStringsToTags")
    default List<Tag> mapStringsToTags(List<String> tagNames) {
        return tagNames.stream()
                .map(name -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tag;
                })
                .toList();
    }

    @Named("mapTagsToStrings")
    default List<String> mapTagsToStrings(List<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .toList();
    }
}