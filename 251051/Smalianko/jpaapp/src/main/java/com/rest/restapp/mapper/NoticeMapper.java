package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import com.rest.restapp.entity.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoticeMapper {

    Notice toEntity(NoticeRequestToDto requestTo);

    @Mapping(source = "issue.id", target = "issueId")
    NoticeResponseToDto toResponseTo(Notice entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(NoticeRequestToDto requestTo, @MappingTarget Notice entity);
}