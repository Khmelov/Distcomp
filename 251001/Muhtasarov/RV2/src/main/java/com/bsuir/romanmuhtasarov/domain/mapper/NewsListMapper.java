package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.request.NewsRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.NewsResponseTo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NewsMapper.class)
public interface NewsListMapper {
    List<News> toNewsList(List<NewsRequestTo> newsRequestToList);

    List<NewsResponseTo> toNewsResponseToList(List<News> newsList);
}
