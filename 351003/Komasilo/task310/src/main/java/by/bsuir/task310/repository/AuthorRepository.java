package by.bsuir.task310.repository;

import by.bsuir.task310.model.Author;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AuthorRepository {
    private final ConcurrentHashMap<Long, Author> authors = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Author save(Author author) {
        long id = idGenerator.incrementAndGet();
        author.setId(id);
        authors.put(id, author);
        return author;
    }

    public List<Author> findAll() {
        return new ArrayList<>(authors.values());
    }

    public Optional<Author> findById(Long id) {
        return Optional.ofNullable(authors.get(id));
    }

    public Author update(Author author) {
        authors.put(author.getId(), author);
        return author;
    }

    public boolean deleteById(Long id) {
        return authors.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return authors.containsKey(id);
    }
}