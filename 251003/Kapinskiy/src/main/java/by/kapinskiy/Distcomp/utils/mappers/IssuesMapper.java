package by.kapinskiy.Distcomp.utils.mappers;

import by.kapinskiy.Distcomp.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.IssueResponseDTO;
import by.kapinskiy.Distcomp.models.Issue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IssuesMapper {

    @Mapping(target = "userId", expression = "java(issue.getUser().getId())")
    @Mapping(target = "tags", expression = "java(issue.getTags().stream().map(tag -> tag.getName()).toList())")
    IssueResponseDTO toIssueResponse(Issue issue);

    List<IssueResponseDTO> toIssueResponseList(List<Issue> issues);

    @Mapping(target = "tags", ignore = true)
    Issue toIssue(IssueRequestDTO issueRequestDTO);
}
