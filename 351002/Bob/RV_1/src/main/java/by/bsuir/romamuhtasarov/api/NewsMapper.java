package by.bsuir.romamuhtasarov.api;

import by.bsuir.romamuhtasarov.impl.bean.News;
import by.bsuir.romamuhtasarov.impl.dto.NewsRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.NewsResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewsMapper {
    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);


    NewsRequestTo NewsToNewsRequestTo(News News);

    NewsResponseTo NewsToNewsResponseTo(News News);

    News NewsResponseToToNews(NewsResponseTo responseTo);

    News NewsRequestToToNews(NewsRequestTo requestTo);
}