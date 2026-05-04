package com.example.task310.mapper;

import com.example.task310.dto.CreatorRequestTo;
import com.example.task310.dto.CreatorResponseTo;
import com.example.task310.model.Creator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreatorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "news", ignore = true)
    Creator toEntity(CreatorRequestTo request);

    CreatorResponseTo toResponse(Creator creator);
}