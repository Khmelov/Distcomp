package com.example.restservice.mapper;

import com.example.restservice.dto.MessageCreateDto;
import com.example.restservice.dto.MessageResponseDto;
import com.example.restservice.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "country", source = "id", qualifiedByName = "createCountry")
    Message toEntity(MessageCreateDto messageCreateDto);

    MessageResponseDto toDto(Message entity);

    @Named("createCountry")
    default String map(long value){
        return "bl";
    }
}
