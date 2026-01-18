package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.IssueRequestTo;
import com.rest.restapp.dto.response.IssueResponseTo;
import com.rest.restapp.entity.Issue;
import com.rest.restapp.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(target = "tags", ignore = true)
    Issue toEntity(IssueRequestTo requestTo);

    @Mapping(source = "user.id", target = "userId")
    IssueResponseTo toResponseTo(Issue entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntityFromDto(IssueRequestTo requestTo, @MappingTarget Issue entity);

    default String map(OffsetDateTime value) {
        return value == null ? null : value.toString();
    }

    default List<String> map(List<Tag> value) {
        if (value == null) { return Collections.emptyList(); }
        return value.stream()
                .map(Tag::toString)
                .toList();
    }

}