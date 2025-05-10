package com.example.distcomp.mapper;

import com.example.distcomp.dto.IssueRequestTo;
import com.example.distcomp.dto.IssueResponseTo;
import com.example.distcomp.model.Issue;
import com.example.distcomp.model.Label;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IssueMapper {
    @Mapping(target = "labels", source = "labels", qualifiedByName = "stringsToLabels")
    Issue toEntity(IssueRequestTo request);
    @Mapping(target = "writerId", source = "entity.writer.id")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "labelsToStrings")
    IssueResponseTo toResponse(Issue entity);

    List<Issue> listToEntity(List<IssueRequestTo> dtoList);

    List<IssueResponseTo> listToResponse(List<Issue> entityList);

    @Named("stringsToLabels")
    default List<Label> map(List<String> value){
        List<Label> res = new ArrayList<>();
        if (value == null){
            return res;
        }
        for (String str:value) {
            Label leb = new Label();
            leb.setName(str);
            leb.setIssues(new HashSet<>());
            res.add(leb);
        }
        return res;
    }
    @Named("labelsToStrings")
    default List<String> mapp(List<Label> labels){
        List<String> res = new ArrayList<>();
        if (labels == null){
            return res;
        }
        for (Label leb:labels) {
            res.add(leb.getName());
        }
        return res;
    }
}
