package com.example.distcomp_1.repository;

import com.example.distcomp_1.mdoel.Author;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class AuthorRepository {
    private Long currentId = 0L;
    private final HashMap<Long, Author> authors = new HashMap<>();

    public Author add(Author author) {
        author.setId(currentId++);
        authors.put(author.getId(), author);
        return author;
    }
    public Author get(long id) {
        return authors.get(id);
    }

    public List<Author> getAuthors() {
        return new ArrayList<>(authors.values());
    }
    public void delete(long id) {
        authors.remove(id);
    }

    public Author update(Author author) {
        authors.put(author.getId(), author);
        return author;
    }
}
