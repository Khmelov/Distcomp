package com.example.restservice.mapper;

import com.example.restservice.dto.MessageCreateDto;
import com.example.restservice.dto.MessageResponseDto;
import com.example.restservice.model.Message;
import com.example.restservice.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "content", source = "dto.content")
    Message toEntity(MessageCreateDto dto, News news);
    @Mapping(target = "newsId", source = "entity.news.id")
    MessageResponseDto toDto(Message entity);
}
