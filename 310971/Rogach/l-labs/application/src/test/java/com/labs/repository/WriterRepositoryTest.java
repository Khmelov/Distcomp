package com.labs.repository;

import com.labs.domain.entity.Writer;
import com.labs.domain.repository.WriterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WriterRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WriterRepository writerRepository;

    @BeforeEach
    void setUp() {
        writerRepository.deleteAll();
    }

    @Test
    void testSave() {
        Writer writer = Writer.builder()
                .login("test@example.com")
                .password("password123")
                .firstname("John")
                .lastname("Doe")
                .build();

        Writer saved = writerRepository.save(writer);

        assertNotNull(saved.getId());
        assertEquals("test@example.com", saved.getLogin());
        assertEquals("John", saved.getFirstname());
        assertEquals("Doe", saved.getLastname());
    }

    @Test
    void testFindById() {
        Writer writer = Writer.builder()
                .login("find@example.com")
                .password("password123")
                .firstname("Jane")
                .lastname("Smith")
                .build();

        Writer saved = writerRepository.save(writer);
        Optional<Writer> found = writerRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("find@example.com", found.get().getLogin());
    }

    @Test
    void testFindAll() {
        writerRepository.save(Writer.builder()
                .login("writer1@example.com")
                .password("password123")
                .firstname("First")
                .lastname("Writer")
                .build());

        writerRepository.save(Writer.builder()
                .login("writer2@example.com")
                .password("password123")
                .firstname("Second")
                .lastname("Writer")
                .build());

        List<Writer> all = writerRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testFindAllWithPagination() {
        for (int i = 1; i <= 5; i++) {
            writerRepository.save(Writer.builder()
                    .login("writer" + i + "@example.com")
                    .password("password123")
                    .firstname("Writer" + i)
                    .lastname("Test")
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<Writer> page = writerRepository.findAll(pageable);

        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void testFindAllWithSorting() {
        writerRepository.save(Writer.builder()
                .login("c@example.com")
                .password("password123")
                .firstname("C")
                .lastname("Writer")
                .build());

        writerRepository.save(Writer.builder()
                .login("a@example.com")
                .password("password123")
                .firstname("A")
                .lastname("Writer")
                .build());

        writerRepository.save(Writer.builder()
                .login("b@example.com")
                .password("password123")
                .firstname("B")
                .lastname("Writer")
                .build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "login"));
        Page<Writer> page = writerRepository.findAll(pageable);

        List<Writer> writers = page.getContent();
        assertEquals("a@example.com", writers.get(0).getLogin());
        assertEquals("b@example.com", writers.get(1).getLogin());
        assertEquals("c@example.com", writers.get(2).getLogin());
    }

    @Test
    void testFindAllWithFiltering() {
        writerRepository.save(Writer.builder()
                .login("john@example.com")
                .password("password123")
                .firstname("John")
                .lastname("Doe")
                .build());

        writerRepository.save(Writer.builder()
                .login("jane@example.com")
                .password("password123")
                .firstname("Jane")
                .lastname("Smith")
                .build());

        Specification<Writer> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("firstname")), "%john%");

        Page<Writer> page = writerRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("john@example.com", page.getContent().get(0).getLogin());
    }

    @Test
    void testFindByLogin() {
        Writer writer = Writer.builder()
                .login("unique@example.com")
                .password("password123")
                .firstname("Unique")
                .lastname("User")
                .build();

        writerRepository.save(writer);

        Optional<Writer> found = writerRepository.findByLogin("unique@example.com");

        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getLogin());
    }

    @Test
    void testUpdate() {
        Writer writer = Writer.builder()
                .login("update@example.com")
                .password("password123")
                .firstname("Original")
                .lastname("Name")
                .build();

        Writer saved = writerRepository.save(writer);
        saved.setFirstname("Updated");
        saved.setLastname("Surname");

        Writer updated = writerRepository.save(saved);

        assertEquals("Updated", updated.getFirstname());
        assertEquals("Surname", updated.getLastname());
    }

    @Test
    void testDelete() {
        Writer writer = Writer.builder()
                .login("delete@example.com")
                .password("password123")
                .firstname("Delete")
                .lastname("Test")
                .build();

        Writer saved = writerRepository.save(writer);
        Long id = saved.getId();

        writerRepository.deleteById(id);

        Optional<Writer> found = writerRepository.findById(id);
        assertFalse(found.isPresent());
    }
}

