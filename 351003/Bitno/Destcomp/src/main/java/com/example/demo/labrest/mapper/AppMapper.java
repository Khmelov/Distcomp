package com.example.demo.labrest.mapper;

import com.example.demo.labrest.dto.*;
import com.example.demo.labrest.model.Creator;
import com.example.demo.labrest.model.Marker;
import com.example.demo.labrest.model.Notice;
import com.example.demo.labrest.model.Topic;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface AppMapper {

    @Named("formatDate")
    default String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null;
    }

    @Mapping(target = "role", source = "req.role", defaultValue = "CUSTOMER")
    Creator toCreator(CreatorRequestTo req);

    @Mapping(target = "role", source = "src.role")
    CreatorResponseTo toCreatorResponse(Creator src);

    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "markerIds", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Topic toTopic(TopicRequestTo req);

    Marker toMarker(MarkerRequestTo req);
    MarkerResponseTo toMarkerResponse(Marker src);

    Notice toNotice(NoticeRequestTo req);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "topicId", source = "topicId")
    @Mapping(target = "content", source = "content")
    NoticeResponseTo toNoticeResponse(Notice notice);

    // Обратный маппинг (если нужен)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "topicId", source = "topicId")
    @Mapping(target = "content", source = "content")
    Notice toNotice(NoticeResponseTo response);

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "markerIds", expression = "java(src.getMarkers().stream().map(Marker::getId).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "created", qualifiedByName = "formatDate")
    @Mapping(target = "modified", qualifiedByName = "formatDate")
    TopicResponseTo toTopicResponseWithRelations(Topic src);
}