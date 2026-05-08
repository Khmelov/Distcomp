package org.polozkov.mapper.issue;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.entity.issue.Issue;

@Mapper(componentModel = "spring")
public interface IssueMapper {

    @Mapping(target = "userId", source =  "user.id")
    IssueResponseTo issueToResponseDto(Issue issue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Issue requestDtoToIssue(IssueRequestTo issueRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Issue updateIssue(@MappingTarget Issue issue, IssueRequestTo issueRequest);
}