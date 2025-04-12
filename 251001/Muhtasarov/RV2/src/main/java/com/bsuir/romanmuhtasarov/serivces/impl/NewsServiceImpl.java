package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Creator;
import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.NewsListMapper;
import com.bsuir.romanmuhtasarov.domain.mapper.NewsMapper;
import com.bsuir.romanmuhtasarov.domain.request.NewsRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.NewsResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchCreatorException;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchNewsException;
import com.bsuir.romanmuhtasarov.serivces.NewsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.repositories.NewsRepository;
import com.bsuir.romanmuhtasarov.serivces.CreatorService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Validated
public class NewsServiceImpl implements NewsService {
    private final CreatorService creatorService;
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final NewsListMapper newsListMapper;

    @Autowired
    public NewsServiceImpl(CreatorService creatorService, NewsRepository newsRepository, NewsMapper newsMapper, NewsListMapper newsListMapper) {
        this.creatorService = creatorService;
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
        this.newsListMapper = newsListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public NewsResponseTo create(@Valid NewsRequestTo entity) {
        Creator creator = creatorService.findCreatorByIdExt(entity.creatorId()).orElseThrow(() -> new NoSuchCreatorException(entity.creatorId()));
        News news = newsMapper.toNews(entity);
        news.setCreator(creator);
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
            news.setCreator(newsRef.getCreator());
            news.setMessageList(newsRef.getMessageList());
            news.setNewsLabelList(newsRef.getNewsLabelList());
            //  newsResponseTo.stickerList() = news.getNewsLabelList().stream().map(element -> stickerMapper.toLabelResponseTo(element.getLabel())).collect(Collectors.toList());
            return newsMapper.toNewsResponseTo(newsRepository.save(news));
        } else {
            throw new NoSuchCreatorException(entity.id());
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
