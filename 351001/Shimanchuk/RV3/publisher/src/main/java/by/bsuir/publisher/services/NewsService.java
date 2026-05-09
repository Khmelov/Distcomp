package by.bsuir.publisher.services;

import by.bsuir.publisher.dto.requests.NewsRequestDto;
import by.bsuir.publisher.dto.responses.NewsResponseDto;

import java.util.List;

public interface NewsService extends BaseService<NewsRequestDto, NewsResponseDto> {
    List<NewsResponseDto> readAll();
}
