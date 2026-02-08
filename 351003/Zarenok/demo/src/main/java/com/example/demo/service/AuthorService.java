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

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    //CREATE
    public AuthorResponseTo create(AuthorRequestTo dto){
        Author author = new Author();

        author.setFirstname(dto.getFirstname());
        author.setLastname(dto.getLastname());
        author.setLogin(dto.getLogin());
        author.setPassword(dto.getPassword());

        Author saved = authorRepository.save(author);

        return new AuthorResponseTo(
                saved.getId(),
                saved.getLogin(),
                saved.getFirstname(),
                saved.getLastname()
        );
    }
    //READ
    public AuthorResponseTo findById(Long id){
        Author entity = authorRepository.findById(id).orElseThrow(()->
                new RuntimeException("Author not found: " + id));

        return new AuthorResponseTo(
                entity.getId(),
                entity.getLogin(),
                entity.getFirstname(),
                entity.getLastname()
        );
    }

    public List<AuthorResponseTo> findAll(){
        return authorRepository.findAll()
                .stream().map(author -> new AuthorResponseTo(
                        author.getId(),
                        author.getLogin(),
                        author.getFirstname(),
                        author.getLastname())).toList();
    }

    //UPDATE
    public AuthorResponseTo update(Long id, AuthorRequestTo dto){
        Author entity = authorRepository.findById(id).orElseThrow(()->
                new RuntimeException("Author not found: " + id));


        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setLogin(dto.getLogin());
        entity.setPassword(dto.getPassword());

        Author updated = authorRepository.save(entity);

        return new AuthorResponseTo(
                entity.getId(),
                entity.getLogin(),
                entity.getFirstname(),
                entity.getLastname()
        );
    }

    public void delete(Long id){
        if(!authorRepository.existsById(id)){
            throw new RuntimeException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }
}
