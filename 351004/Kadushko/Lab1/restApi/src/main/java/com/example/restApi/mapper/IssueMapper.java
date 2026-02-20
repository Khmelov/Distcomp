package com.example.restApi.mapper;

import com.example.restApi.dto.request.IssueRequestTo;
import com.example.restApi.dto.response.IssueResponseTo;
import com.example.restApi.model.Issue;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IssueMapper {
    Issue toEntity(IssueRequestTo request);
    IssueResponseTo toDto(Issue issue);
    void updateEntity(@MappingTarget Issue issue, IssueRequestTo request);
}
