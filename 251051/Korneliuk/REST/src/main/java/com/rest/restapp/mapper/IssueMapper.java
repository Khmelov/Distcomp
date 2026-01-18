package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import com.rest.restapp.entity.Issue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    Issue toEntity(IssueRequestToDto requestTo);

    @Mapping(source = "user.id", target = "userId")
    IssueResponseToDto toResponseTo(Issue entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntityFromDto(IssueRequestToDto requestTo, @MappingTarget Issue entity);

    default String map(OffsetDateTime value) {
        return value == null ? null : value.toString();
    }
}