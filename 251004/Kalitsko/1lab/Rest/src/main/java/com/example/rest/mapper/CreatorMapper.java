package com.example.rest.mapper;

import com.example.rest.dto.CreatorRequestTo;
import com.example.rest.dto.CreatorResponseTo;
import com.example.rest.entity.Creator;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreatorMapper {

    Creator toCreator(CreatorRequestTo creatorRequestTo);
    CreatorResponseTo toCreatorResponseTo(Creator creator);
}

