package com.example.restapp.mapper;

import com.example.restapp.dto.request.ArticleRequestTo;
import com.example.restapp.dto.response.ArticleResponseTo;
import com.example.restapp.model.Article;
import com.example.restapp.model.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "stickers", ignore = true) // Handled in Service
    @Mapping(target = "author", ignore = true)   // Handled in Service
    Article toEntity(ArticleRequestTo request);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "stickers", target = "stickerIds", qualifiedByName = "stickersToIds")
    ArticleResponseTo toResponse(Article entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "stickers", ignore = true) // Handled in Service
    @Mapping(target = "author", ignore = true)   // Handled in Service
    void updateEntityFromDto(ArticleRequestTo request, @MappingTarget Article entity);

    @Named("stickersToIds")
    default List<Long> stickersToIds(List<Sticker> stickers) {
        if (stickers == null) return null;
        return stickers.stream().map(Sticker::getId).collect(Collectors.toList());
    }
}