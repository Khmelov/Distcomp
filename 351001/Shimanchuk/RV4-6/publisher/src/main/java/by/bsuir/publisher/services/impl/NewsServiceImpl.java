package by.bsuir.publisher.services.impl;

import by.bsuir.publisher.domain.News;
import by.bsuir.publisher.dto.requests.NewsRequestDto;
import by.bsuir.publisher.dto.requests.converters.NewsRequestConverter;
import by.bsuir.publisher.dto.responses.NewsResponseDto;
import by.bsuir.publisher.dto.responses.converters.CollectionNewsResponseConverter;
import by.bsuir.publisher.dto.responses.converters.NewsResponseConverter;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.repositories.NewsRepository;
import by.bsuir.publisher.services.NewsService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(rollbackFor = {EntityExistsException.class, NoEntityExistsException.class})
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsRequestConverter newsRequestConverter;
    private final NewsResponseConverter newsResponseConverter;
    private final CollectionNewsResponseConverter collectionNewsResponseConverter;

    @Override
    @Validated
    public NewsResponseDto create(@Valid @NonNull NewsRequestDto dto) throws EntityExistsException {
        Optional<News> news = dto.getId() == null ? Optional.empty() : newsRepository.findById(dto.getId());
        if (news.isEmpty()) {
            return newsResponseConverter.toDto(newsRepository.save(newsRequestConverter.fromDto(dto)));
        } else {
            throw new EntityExistsException(Comments.EntityExistsException);
        }
    }

    @Override
    public Optional<NewsResponseDto> read(@NonNull Long id) {
        return newsRepository.findById(id).flatMap(news -> Optional.of(
                newsResponseConverter.toDto(news)));
    }

    @Override
    @Validated
    public NewsResponseDto update(@Valid @NonNull NewsRequestDto dto) throws NoEntityExistsException {
        Optional<News> news = dto.getId() == null || newsRepository.findById(dto.getId()).isEmpty() ?
                Optional.empty() : Optional.of(newsRequestConverter.fromDto(dto));
        return newsResponseConverter.toDto(newsRepository.save(news.orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException))));
    }

    @Override
    public Long delete(@NonNull Long id) throws NoEntityExistsException {
        Optional<News> news = newsRepository.findById(id);
        newsRepository.deleteById(news.map(News::getId).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
        return news.get().getId();
    }

    @Override
    public List<NewsResponseDto> readAll() {
        return collectionNewsResponseConverter.toListDto(newsRepository.findAll());
    }
}
