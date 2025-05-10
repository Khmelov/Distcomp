package com.example.restservice.mapper;

import com.example.restservice.dto.NewsCreateDto;
import com.example.restservice.dto.NewsResponseDto;
import com.example.restservice.model.Author;
import com.example.restservice.model.Marker;
import com.example.restservice.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "markers", source = "markers")
    News toEntity(NewsCreateDto dto, Author author, List<Marker> markers);
    @Mapping(target = "authorId", source = "author.id")
    NewsResponseDto toDto(News entity);
}
