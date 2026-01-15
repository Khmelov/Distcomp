package com.example.task320jpa.mapper;

import com.example.task320jpa.dto.request.MarkRequestTo;
import com.example.task320jpa.dto.response.MarkResponseTo;
import com.example.task320jpa.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct маппер для Mark
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MarkMapper {
    
    /**
     * Преобразовать MarkRequestTo в Mark
     */
    Mark toEntity(MarkRequestTo requestTo);
    
    /**
     * Преобразовать Mark в MarkResponseTo
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MarkResponseTo toResponseTo(Mark mark);
    
    /**
     * Обновить существующий Mark из MarkRequestTo (для PATCH операций)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequestTo(MarkRequestTo requestTo, @MappingTarget Mark mark);
}
