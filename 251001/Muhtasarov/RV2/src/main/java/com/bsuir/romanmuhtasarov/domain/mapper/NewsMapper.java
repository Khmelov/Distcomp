package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.request.NewsRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.NewsResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MessageListMapper.class, LabelListMapper.class, CreatorMapper.class})
public interface NewsMapper {
    @Mapping(source = "creatorId", target = "creator.id")
    News toNews(NewsRequestTo newsRequestTo);
    @Mapping(source = "creator.id", target = "creatorId")
    NewsResponseTo toNewsResponseTo(News news);
}
