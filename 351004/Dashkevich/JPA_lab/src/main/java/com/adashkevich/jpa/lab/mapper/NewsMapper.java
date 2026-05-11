package com.adashkevich.jpa.lab.mapper;

import com.adashkevich.jpa.lab.dto.request.NewsRequestTo;
import com.adashkevich.jpa.lab.dto.response.NewsResponseTo;
import com.adashkevich.jpa.lab.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    News toEntity(NewsRequestTo dto);

    @Mapping(target = "created", expression = "java(entity.getCreated() != null ? entity.getCreated().toString() : null)")
    @Mapping(target = "modified", expression = "java(entity.getModified() != null ? entity.getModified().toString() : null)")
    NewsResponseTo toResponse(News entity);
}
