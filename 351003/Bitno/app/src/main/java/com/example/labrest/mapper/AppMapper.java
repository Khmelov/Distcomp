package com.example.labrest.mapper;

import com.example.labrest.dto.*;
import com.example.labrest.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppMapper {
    Creator toCreator(CreatorRequestTo req);
    CreatorResponseTo toCreatorResponse(Creator src);

    Topic toTopic(TopicRequestTo req);
    TopicResponseTo toTopicResponse(Topic src);

    Marker toMarker(MarkerRequestTo req);
    MarkerResponseTo toMarkerResponse(Marker src);

    Notice toNotice(NoticeRequestTo req);
    NoticeResponseTo toNoticeResponse(Notice src);
}