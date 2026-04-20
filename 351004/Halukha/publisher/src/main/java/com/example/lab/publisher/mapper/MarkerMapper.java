package com.example.lab.publisher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab.publisher.dto.MarkerRequestTo;
import com.example.lab.publisher.dto.MarkerResponseTo;
import com.example.lab.publisher.model.Marker;

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
