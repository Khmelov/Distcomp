package com.adashkevich.redis.lab.mapper;

import com.adashkevich.redis.lab.dto.request.MarkerRequestTo;
import com.adashkevich.redis.lab.dto.response.MarkerResponseTo;
import com.adashkevich.redis.lab.model.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);
    MarkerResponseTo toResponse(Marker entity);
}
