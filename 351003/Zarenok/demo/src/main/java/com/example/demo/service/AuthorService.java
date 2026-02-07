package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final EntityMapper mapper;

    public AuthorService(AuthorRepository authorRepository, EntityMapper mapper) {
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    //CREATE
    public AuthorResponseTo create(AuthorRequestTo dto){
        Author author = mapper.toEntity(dto);
        // сохранение в InMemory Map
        Author saved = authorRepository.save(author);
        return mapper.toResponse(author);
    }
    //READ
    public AuthorResponseTo findById(Long id){
        Author entity = authorRepository.findById(id).orElseThrow(()->
                new RuntimeException("Author not found: " + id));
        return mapper.toResponse(entity);
    }

    public List<AuthorResponseTo> findAll(){
        return authorRepository.findAll()
                .stream().map(mapper::toResponse).toList();
    }

    //UPDATE
    public AuthorResponseTo update(Long id, AuthorRequestTo request){
        Author entity = authorRepository.findById(id).orElseThrow(()->
                new RuntimeException("Author not found: " + id));
        mapper.updateEntity(request, entity);
        Author updated = authorRepository.save(entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id){
        if(!authorRepository.existsById(id)){
            throw new RuntimeException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }
}
