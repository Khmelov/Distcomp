package com.example.discussion.service;


import com.example.discussion.dto.ArticleRequestTo;
import com.example.discussion.dto.ArticleResponseTo;
import com.example.discussion.mapper.ArticleMapper;
import com.example.discussion.model.Article;
import com.example.discussion.model.Creator;
import com.example.discussion.model.Mark;
import com.example.discussion.repository.ArticleRepository;
import com.example.discussion.repository.CreatorRepository;
import com.example.discussion.repository.MarkRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articlesRepository;
    private  final CreatorRepository creatorRepository;
    private  final MarkRepository markRepository;
    private final ArticleMapper articlesMapper = Mappers.getMapper(ArticleMapper.class);

    public ArticleService(ArticleRepository articlesRepository, CreatorRepository creatorRepository, MarkRepository markRepository) {
        this.articlesRepository = articlesRepository;
        this.creatorRepository = creatorRepository;
        this.markRepository = markRepository;
    }

    public List<ArticleResponseTo> findAll() {
        return articlesRepository.findAll().stream()
                .map(articlesMapper::toDto)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo findById(Long id) {
        Optional<Article> articles = articlesRepository.findById(id);
        return articles.map(articlesMapper::toDto).orElse(null);
    }
    @Transactional
    public ArticleResponseTo save(ArticleRequestTo articlesRequestTo) {
        if (articlesRepository.existsByTitle(articlesRequestTo.getTitle())) {
            throw new EntityExistsException("A articles with the same title already exists.");
        }
        Long creatorId = articlesRequestTo.getCreatorId();
        Optional<Creator> creator = creatorRepository.findById(creatorId);
        if (!creator.isPresent()) {
            throw new EntityNotFoundException("Author not found with id " + creatorId);
        }
        Article articles = articlesMapper.toEntity(articlesRequestTo);
        String[] marks = articlesRequestTo.getMarks();
        if(marks != null && marks.length != 0){
            for(String markName : marks){
                Optional<Mark> mark = markRepository.findByName(markName);
                if(articles.getMarks() == null){
                    articles.setMarks(new ArrayList<>());
                }
                if(mark.isPresent()){
                    articles.getMarks().add(mark.get());
                }
                else{
                    Mark articleMark = new Mark();
                    articleMark.setName(markName);
                    Mark savedMark = markRepository.save(articleMark);
                    articles.getMarks().add(savedMark);
                }
            }
        }
        articles.setCreator(creator.get());
        LocalDateTime currentDate = LocalDateTime.now();
//        articles.setCreated(currentDate);
        articles.setModified(currentDate);
        Article savedArticles = articlesRepository.save(articles);
        System.out.println(savedArticles.toString());
        return articlesMapper.toDto(savedArticles);
    }


    public ArticleResponseTo update(ArticleRequestTo articlesRequestTo) {
        Article existingArticles = articlesRepository.findById(articlesRequestTo.getId()).orElseThrow(() -> new RuntimeException("Articles not found"));
        articlesMapper.updateEntityFromDto(articlesRequestTo, existingArticles);
        Article updatedArticles = articlesRepository.save(existingArticles);
        return articlesMapper.toDto(updatedArticles);
    }

    public void deleteById(Long id) {
        if (!articlesRepository.existsById(id)) {
            throw new EntityNotFoundException("Articles not found with id " + id);
        }
        articlesRepository.deleteById(id);
    }
}