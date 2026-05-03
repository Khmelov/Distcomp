package com.example.Task310.mapper;

import com.example.Task310.bean.Marker;
import com.example.Task310.dto.MarkerRequestTo;
import com.example.Task310.dto.MarkerResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    MarkerResponseTo toResponse(Marker marker);
    Marker toEntity(MarkerRequestTo request);

    // ВАЖНО: игнорируем id, чтобы Hibernate не выдавал ошибку 500
    @Mapping(target = "id", ignore = true)
    void updateEntity(MarkerRequestTo request, @MappingTarget Marker marker);
}