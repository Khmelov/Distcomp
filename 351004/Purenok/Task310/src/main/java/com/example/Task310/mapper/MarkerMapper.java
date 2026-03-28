package com.example.Task310.mapper;

import com.example.Task310.bean.Marker;
import com.example.Task310.dto.MarkerRequestTo;
import com.example.Task310.dto.MarkerResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);
    MarkerResponseTo toDto(Marker entity);
    void updateEntityFromDto(MarkerRequestTo dto, @MappingTarget Marker entity);
}