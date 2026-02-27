package com.example.lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab.dto.MarkerRequestTo;
import com.example.lab.dto.MarkerResponseTo;
import com.example.lab.model.Marker;

@Mapper
public interface MarkerMapper {

    MarkerMapper INSTANCE = Mappers.getMapper(MarkerMapper.class);

    @Mapping(target = "id", ignore = true)
    Marker toEntity(MarkerRequestTo dto);

    MarkerResponseTo toDto(Marker entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "dto.name"),
    })
    Marker updateEntity(MarkerRequestTo dto, Marker existing);
}
