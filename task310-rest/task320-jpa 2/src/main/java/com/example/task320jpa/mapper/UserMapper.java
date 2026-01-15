package com.example.task320jpa.mapper;

import com.example.task320jpa.dto.request.UserRequestTo;
import com.example.task320jpa.dto.response.UserResponseTo;
import com.example.task320jpa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct маппер для User
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    /**
     * Преобразовать UserRequestTo в User
     */
    User toEntity(UserRequestTo requestTo);
    
    /**
     * Преобразовать User в UserResponseTo
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname", source = "lastname")
    UserResponseTo toResponseTo(User user);
    
    /**
     * Обновить существующий User из UserRequestTo (для PATCH операций)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequestTo(UserRequestTo requestTo, @MappingTarget User user);
}
