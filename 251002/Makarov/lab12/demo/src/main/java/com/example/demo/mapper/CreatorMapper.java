package com.example.demo.mapper;

import com.example.demo.dto.CreatorRequestTo;
import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.model.Creator;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CreatorMapper {
    Creator toEntity(CreatorRequestTo dto);
    CreatorResponseTo toDto(Creator entity);
    void updateEntityFromDto(CreatorRequestTo request, @MappingTarget Creator entity);
}
