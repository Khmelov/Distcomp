package com.labs.repository;

import com.labs.domain.entity.Label;
import com.labs.domain.repository.LabelRepository;
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

class LabelRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();
    }

    @Test
    void testSave() {
        Label label = Label.builder()
                .name("Java")
                .build();

        Label saved = labelRepository.save(label);

        assertNotNull(saved.getId());
        assertEquals("Java", saved.getName());
    }

    @Test
    void testFindById() {
        Label label = Label.builder()
                .name("Python")
                .build();

        Label saved = labelRepository.save(label);
        Optional<Label> found = labelRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Python", found.get().getName());
    }

    @Test
    void testFindAllWithPagination() {
        for (int i = 1; i <= 5; i++) {
            labelRepository.save(Label.builder()
                    .name("Label" + i)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<Label> page = labelRepository.findAll(pageable);

        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void testFindAllWithSorting() {
        labelRepository.save(Label.builder().name("C").build());
        labelRepository.save(Label.builder().name("A").build());
        labelRepository.save(Label.builder().name("B").build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Label> page = labelRepository.findAll(pageable);

        List<Label> labels = page.getContent();
        assertEquals("A", labels.get(0).getName());
        assertEquals("B", labels.get(1).getName());
        assertEquals("C", labels.get(2).getName());
    }

    @Test
    void testFindAllWithFiltering() {
        labelRepository.save(Label.builder().name("Java").build());
        labelRepository.save(Label.builder().name("JavaScript").build());
        labelRepository.save(Label.builder().name("Python").build());

        Specification<Label> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%java%");

        Page<Label> page = labelRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .anyMatch(l -> l.getName().equals("Java")));
        assertTrue(page.getContent().stream()
                .anyMatch(l -> l.getName().equals("JavaScript")));
    }

    @Test
    void testFindByName() {
        Label label = Label.builder()
                .name("UniqueLabel")
                .build();

        labelRepository.save(label);

        Optional<Label> found = labelRepository.findByName("UniqueLabel");

        assertTrue(found.isPresent());
        assertEquals("UniqueLabel", found.get().getName());
    }

    @Test
    void testFindByNameIn() {
        labelRepository.save(Label.builder().name("Label1").build());
        labelRepository.save(Label.builder().name("Label2").build());
        labelRepository.save(Label.builder().name("Label3").build());

        List<Label> found = labelRepository.findByNameIn(List.of("Label1", "Label3"));

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(l -> l.getName().equals("Label1")));
        assertTrue(found.stream().anyMatch(l -> l.getName().equals("Label3")));
    }

    @Test
    void testUpdate() {
        Label label = Label.builder()
                .name("Original")
                .build();

        Label saved = labelRepository.save(label);
        saved.setName("Updated");

        Label updated = labelRepository.save(saved);

        assertEquals("Updated", updated.getName());
    }

    @Test
    void testDelete() {
        Label label = Label.builder()
                .name("DeleteMe")
                .build();

        Label saved = labelRepository.save(label);
        Long id = saved.getId();

        labelRepository.deleteById(id);

        Optional<Label> found = labelRepository.findById(id);
        assertFalse(found.isPresent());
    }
}

