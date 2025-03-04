package com.bsuir.dc.service;

import com.bsuir.dc.dto.request.AuthorRequestTo;
import com.bsuir.dc.dto.response.AuthorResponseTo;
import com.bsuir.dc.model.Author;
import com.bsuir.dc.repository.AuthorRepository;
import com.bsuir.dc.util.exception.EntityNotFoundException;
import com.bsuir.dc.util.mapper.AuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Transactional
    public AuthorResponseTo save(AuthorRequestTo authorRequestTo) {
        Author author = authorMapper.toAuthor(authorRequestTo);
        return authorMapper.toCreatorResponse(authorRepository.save(author));
    }

    @Transactional(readOnly = true)
    public List<AuthorResponseTo> findAll() { return authorMapper.toAuthorResponseList(authorRepository.findAll()); }

    @Transactional(readOnly = true)
    public AuthorResponseTo findById(long id){
        Author author = authorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Author not found"));
        return authorMapper.toCreatorResponse(author);
    }

    @Transactional
    public AuthorResponseTo update(long id, AuthorRequestTo authorRequestTo) {
        authorRequestTo.setId(id);
        return update(authorRequestTo);
    }

    @Transactional
    public AuthorResponseTo update(AuthorRequestTo authorRequestTo) {
        Author author = authorMapper.toAuthor(authorRequestTo);
        if (!authorRepository.existsById(author.getId())) { throw new EntityNotFoundException("Author not found"); }
        return authorMapper.toCreatorResponse(authorRepository.save(author));
    }

    @Transactional
    public void deleteById(long id) {
        if (!authorRepository.existsById(id)) { throw new EntityNotFoundException("Author not found"); }
        authorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByLogin(String login){ return authorRepository.existsByLogin(login); }
}
