package com.distcomp.mapper;

import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;
import com.distcomp.model.Issue;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

/**
 * Converts {@link Issue} entities to and from their transport objects.
 *
 * <p>The author and the tags are associations: they are resolved by the service
 * layer and therefore ignored during the plain field mapping.</p>
 */
@Mapper(componentModel = "spring", uses = TagMapper.class)
public interface IssueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Issue toEntity(IssueRequestTo request);

    @Mapping(target = "creatorId", source = "creator.id")
    IssueResponseTo toResponse(Issue issue);

    List<IssueResponseTo> toResponseList(Collection<Issue> issues);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntity(IssueRequestTo request, @MappingTarget Issue issue);
}
