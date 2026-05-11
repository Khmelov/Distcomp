package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Writer;
import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.NewsListMapper;
import com.bsuir.romanmuhtasarov.domain.mapper.NewsMapper;
import com.bsuir.romanmuhtasarov.domain.request.NewsRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.NewsResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchWriterException;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchNewsException;
import com.bsuir.romanmuhtasarov.serivces.NewsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.repositories.NewsRepository;
import com.bsuir.romanmuhtasarov.serivces.WriterService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Validated
public class NewsServiceImpl implements NewsService {
    private final WriterService writerService;
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final NewsListMapper newsListMapper;

    @Autowired
    public NewsServiceImpl(WriterService writerService, NewsRepository newsRepository, NewsMapper newsMapper, NewsListMapper newsListMapper) {
        this.writerService = writerService;
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
        this.newsListMapper = newsListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public NewsResponseTo create(@Valid NewsRequestTo entity) {
        Writer writer = writerService.findWriterByIdExt(entity.writerId()).orElseThrow(() -> new NoSuchWriterException(entity.writerId()));
        News news = newsMapper.toNews(entity);
        news.setWriter(writer);
        return newsMapper.toNewsResponseTo(newsRepository.save(news));
    }

    @Override
    public List<NewsResponseTo> read() {
        return newsListMapper.toNewsResponseToList(newsRepository.findAll());
    }

    @Override
    @Validated(ValidationMarker.OnUpdate.class)
    public NewsResponseTo update(@Valid NewsRequestTo entity) {
        if (newsRepository.existsById(entity.id())) {
            News news = newsMapper.toNews(entity);
            News newsRef = newsRepository.getReferenceById(news.getId());
            news.setWriter(newsRef.getWriter());
            news.setCommentList(newsRef.getCommentList());
            news.setNewsTagList(newsRef.getNewsTagList());
            //  newsResponseTo.stickerList() = news.getNewsTagList().stream().map(element -> stickerMapper.toTagResponseTo(element.getTag())).collect(Collectors.toList());
            return newsMapper.toNewsResponseTo(newsRepository.save(news));
        } else {
            throw new NoSuchWriterException(entity.id());
        }
    }

    @Override
    public void delete(Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
        } else {
            throw new NoSuchNewsException(id);
        }

    }

    @Override
    public NewsResponseTo findNewsById(Long id) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NoSuchNewsException(id));
        return newsMapper.toNewsResponseTo(news);
    }

    @Override
    public Optional<News> findNewsByIdExt(Long id) {
        return newsRepository.findById(id);
    }
}
