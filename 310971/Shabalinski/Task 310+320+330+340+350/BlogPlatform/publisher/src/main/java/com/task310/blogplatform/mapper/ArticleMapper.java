package com.task310.blogplatform.mapper;

import com.task310.blogplatform.dto.ArticleRequestTo;
import com.task310.blogplatform.dto.ArticleResponseTo;
import com.task310.blogplatform.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Article toEntity(ArticleRequestTo dto);

    default ArticleResponseTo toResponseDto(Article entity) {
        if (entity == null) {
            return null;
        }
        ArticleResponseTo dto = new ArticleResponseTo();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        if (entity.getLabels() != null) {
            dto.setLabelIds(entity.getLabels().stream()
                    .map(label -> label.getId())
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    List<ArticleResponseTo> toResponseDtoList(List<Article> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "labels", ignore = true)
    void updateEntityFromDto(ArticleRequestTo dto, @MappingTarget Article entity);
}

