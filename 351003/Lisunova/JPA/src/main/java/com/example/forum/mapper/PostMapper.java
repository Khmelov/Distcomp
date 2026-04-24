package com.example.forum.mapper;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic", ignore = true) // Игнорируем объект, так как сетаем его вручную в сервисе
    Post toEntity(PostRequestTo dto);

    // Указываем MapStruct'у, откуда брать topicId
    @Mapping(target = "topicId", source = "topic.id")
    PostResponseTo toResponse(Post entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic", ignore = true)
    void updateEntityFromDto(PostRequestTo dto, @MappingTarget Post entity);
}