package com.example.discussion.mapper;


import com.example.discussion.dto.CreatorRequestTo;
import com.example.discussion.dto.CreatorResponseTo;
import com.example.discussion.model.Creator;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CreatorMapper {
    Creator toEntity(CreatorRequestTo dto);
    CreatorResponseTo toDto(Creator entity);
    void updateEntityFromDto(CreatorRequestTo request, @MappingTarget Creator entity);
}
