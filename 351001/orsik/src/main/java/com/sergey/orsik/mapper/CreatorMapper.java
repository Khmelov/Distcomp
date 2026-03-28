package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreatorMapper {

    Creator toEntity(CreatorRequestTo request);

    CreatorResponseTo toResponse(Creator entity);
}
