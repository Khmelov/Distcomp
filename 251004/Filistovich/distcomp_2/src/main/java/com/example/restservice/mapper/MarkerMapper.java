package com.example.restservice.mapper;

import com.example.restservice.dto.MarkerCreateDto;
import com.example.restservice.dto.MarkerResponseDto;
import com.example.restservice.model.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerCreateDto dto);
    MarkerResponseDto toDto(Marker entity);
}
