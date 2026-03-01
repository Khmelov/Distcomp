package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final EntityMapper mapper;

    public AuthorService(AuthorRepository authorRepository, EntityMapper mapper) {
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    //CREATE
    public AuthorResponseTo create(AuthorRequestTo dto){
        Author entity = mapper.toEntity(dto);
        Author saved = authorRepository.save(entity);
        return mapper.toAuthorResponse(saved);
    }
    //READ
    public AuthorResponseTo findById(Long id){
        Author entity = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found: " + id));
        return mapper.toAuthorResponse(entity);
    }

    public Page<AuthorResponseTo> findAll(Pageable pageable){
        return authorRepository.findAll(pageable)
                .map(mapper::toAuthorResponse);
    }

    //UPDATE
    public AuthorResponseTo update(Long id, AuthorRequestTo dto){
        Author entity = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found: " + id));

        mapper.updateAuthor(dto, entity);
        Author updated = authorRepository.save(entity);
        return mapper.toAuthorResponse(updated);
    }

    public void delete(Long id){
        if(!authorRepository.existsById(id)){
            throw new NotFoundException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }
}
