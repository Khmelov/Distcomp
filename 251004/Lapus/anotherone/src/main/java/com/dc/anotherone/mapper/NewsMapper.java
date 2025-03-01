package com.dc.anotherone.mapper;

import com.dc.anotherone.model.blo.News;
import com.dc.anotherone.model.dto.NewsRequestTo;
import com.dc.anotherone.model.dto.NewsResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Collection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {

    News mapToBlo(NewsRequestTo NewsRequestTo);

    @Mapping(target = "authorId", source = "News.author.id")
    NewsResponseTo mapToDto(News News);

    Collection<News> mapToListBlo(Collection<NewsRequestTo> kList);

    Collection<NewsResponseTo> mapToListDto(Collection<News> tList);

}
