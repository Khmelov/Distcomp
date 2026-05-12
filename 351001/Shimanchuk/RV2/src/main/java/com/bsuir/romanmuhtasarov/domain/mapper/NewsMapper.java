package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.request.NewsRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.NewsResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommentListMapper.class, TagListMapper.class, WriterMapper.class})
public interface NewsMapper {
    @Mapping(source = "writerId", target = "writer.id")
    News toNews(NewsRequestTo newsRequestTo);
    @Mapping(source = "writer.id", target = "writerId")
    NewsResponseTo toNewsResponseTo(News news);
}
