package com.distcomp.mapper;

import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;
import com.distcomp.model.Issue;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Converts {@link Issue} entities to and from their transport objects.
 *
 * <p>Life-cycle timestamps are produced by the service layer and can never be
 * injected from the outside world.</p>
 */
@Mapper(componentModel = "spring")
public interface IssueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Issue toEntity(IssueRequestTo request);

    IssueResponseTo toResponse(Issue issue);

    List<IssueResponseTo> toResponseList(List<Issue> issues);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntity(IssueRequestTo request, @MappingTarget Issue issue);
}
