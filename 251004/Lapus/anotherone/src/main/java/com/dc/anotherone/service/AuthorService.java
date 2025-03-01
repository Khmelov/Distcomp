package com.dc.anotherone.service;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.model.blo.Author;
import com.dc.anotherone.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AuthorService{

    @Autowired
    private AuthorRepository repository;

    public Collection<Author> getAll() {
        return repository.findAll();
    }

    public Author getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ServiceException("Пользователь не найден", 404));
    }

    public Author save(Author author) {
        if(repository.existsByLogin(author.getLogin()))
            throw new ServiceException("Пользователь не найден", 403);
        if(!validator(author))
        {
            return null;
        }
        return repository.save(author);
    }

    public Author update(Author author) {
        if(!validator(author))
        {
            return null;
        }
        return repository.save(author);
    }

    public void delete(Long id) {
        repository.findById(id).orElseThrow(() -> new ServiceException("Нечего удалить", 404));
        repository.deleteById(id);
    }

    private boolean validator(Author author){
        if (author.getLogin().length() < 2 || author.getLogin().length() > 64
                || author.getPassword().length() < 8 || author.getPassword().length() > 128
                || author.getFirstname().length() < 2 || author.getFirstname().length() > 64
                || author.getLastname().length() < 2 || author.getLastname().length() > 64
        ) {
            return false;
        }
        return true;
    }
}
