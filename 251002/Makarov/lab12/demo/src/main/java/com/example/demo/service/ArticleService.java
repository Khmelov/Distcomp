package com.example.demo.service;

import com.example.demo.dto.ArticleRequestTo;
import com.example.demo.dto.ArticleResponseTo;
import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.error.TitleAlreadyExistsException;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.model.Article;
import com.example.demo.model.Creator;
import com.example.demo.model.Mark;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CreatorRepository;
import com.example.demo.repository.MarkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository repository;
    private final ArticleMapper mapper;
    private final MarkRepository markerRepository;
    private final CreatorRepository creatorRepository;

    public ArticleResponseTo createArticle(ArticleRequestTo request) {
        if (repository.existsByTitle(request.getTitle())) {
            throw new TitleAlreadyExistsException("Article with this title already exists");
        }

        Long creatorId = request.getCreatorId();
        Optional<Creator> creator = creatorRepository.findById(creatorId);
        if(creator.isEmpty()){
            throw new EntityNotFoundException("creator not found");
        }
        Article article = mapper.toEntity(request);

        String[] markers = request.getMarks();
        if(markers!=null&& markers.length != 0){
            for(String markerName : markers){
                Optional<Mark> mark = markerRepository.findByName(markerName);
                if(article.getMarks() == null){
                    article.setMarks(new ArrayList<>());
                }
                if(mark.isPresent()){
                    article.getMarks().add(mark.get());
                }
                else {
                    Mark newMark = new Mark();
                    newMark.setName(markerName);
                    Mark savedMark = markerRepository.save(newMark);
                    article.getMarks().add(savedMark);
                }
            }
        }

        article.setCreator(creator.get());
        article = repository.save(article);
        return mapper.toDto(article);
    }

    public ArticleResponseTo getArticleById(Long id) {
        Article article = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Article not found"));
        return mapper.toDto(article);
    }

    public ArticleResponseTo updateArticle(ArticleRequestTo request){
        Article article = repository.findById(request.getId()).orElseThrow(()->new EntityNotFoundException("Article not found"));
        mapper.updateEntityFromDto(request, article);
        Article updatedArticle = repository.save(article);
        return mapper.toDto(updatedArticle);
    }
    public List<ArticleResponseTo> getAllArticles(){
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    public void deleteArticle(Long id){
        Article article = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        List<Mark> marks = new ArrayList<>(article.getMarks());
        article.getMarks().clear();
        repository.save(article);

        repository.delete(article);

        for (Mark mark : marks) {
            if (mark.getArticles().size() <= 1) {
                markerRepository.delete(mark);
            }
        }
    }

}
