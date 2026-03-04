package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.exception.DuplicateException;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        if (authorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Login already exists");
        }
        Author entity = mapper.toEntity(dto);
        Author saved = authorRepository.save(entity);
        return mapper.toAuthorResponse(saved);
    }
    //READ
    public AuthorResponseTo findById(Long id)
            throws ChangeSetPersister.NotFoundException {

        Author entity = authorRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return mapper.toAuthorResponse(entity);
    }

    public List<AuthorResponseTo> findAll() {
        return authorRepository.findAll().stream()
                .map(mapper::toAuthorResponse)
                .collect(Collectors.toList());
    }

    //UPDATE
    public AuthorResponseTo update(Long id, AuthorRequestTo dto)
            throws ChangeSetPersister.NotFoundException {

        if (authorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Login already exists");
        }

        Author entity = authorRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        mapper.updateAuthor(dto, entity);
        Author updated = authorRepository.save(entity);

        return mapper.toAuthorResponse(updated);
    }

    public void delete(Long id)
            throws ChangeSetPersister.NotFoundException {

        if (!authorRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException();
        }

        authorRepository.deleteById(id);
    }
}
