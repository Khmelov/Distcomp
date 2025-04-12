package org.ex.distributed_computing.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.NewsRequestDTO;
import org.ex.distributed_computing.dto.response.NewsResponseDTO;
import org.ex.distributed_computing.exception.DuplicateDatabaseValueException;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.mapper.NewsMapper;
import org.ex.distributed_computing.model.Author;
import org.ex.distributed_computing.model.News;
import org.ex.distributed_computing.repository.AuthorRepository;
import org.ex.distributed_computing.repository.NewsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService {

  private final NewsRepository newsRepository;
  private final AuthorRepository authorRepository;
  private final NewsMapper newsMapper;

  public List<NewsResponseDTO> getAllNews() {
    return newsMapper.toDtoList(newsRepository.findAll());
  }

  public NewsResponseDTO getNewsById(Long id) {
    News news = newsRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("News not found"));
    return newsMapper.toDto(news);
  }

  public NewsResponseDTO createNews(NewsRequestDTO requestDTO) {
    Author author = authorRepository.findById(requestDTO.authorId())
        .orElseThrow(() -> new NotFoundException("Author not found"));

    if (newsRepository.existsByTitle(requestDTO.title())) {
      throw new DuplicateDatabaseValueException();
    }

    News news = new News();
    news.setAuthor(author);
    news.setTitle(requestDTO.title());
    news.setContent(requestDTO.content());
    news.setCreatedDateTime(LocalDateTime.now());
    news.setUpdatedDateTime(LocalDateTime.now());

    newsRepository.save(news);
    return newsMapper.toDto(news);
  }

  public NewsResponseDTO updateNews(NewsRequestDTO requestDTO) {
    News news = newsRepository.findById(requestDTO.id())
        .orElseThrow(() -> new NotFoundException("News not found"));

    Author author = authorRepository.findById(requestDTO.authorId())
        .orElseThrow(() -> new NotFoundException("Author not found"));

    news.setAuthor(author);
    news.setTitle(requestDTO.title());
    news.setContent(requestDTO.content());
    news.setUpdatedDateTime(LocalDateTime.now());

    newsRepository.save(news);
    return newsMapper.toDto(news);
  }

  public void deleteNews(Long id) {
    News news = newsRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("News not found"));
    newsRepository.delete(news);
  }
}

