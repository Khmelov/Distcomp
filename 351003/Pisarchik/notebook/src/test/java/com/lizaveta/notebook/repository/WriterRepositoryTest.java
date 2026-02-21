package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import com.lizaveta.notebook.model.entity.Writer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class WriterRepositoryTest extends PostgresTestContainerConfig {

    @Autowired
    private WriterRepository writerRepository;

    @Test
    void save_ShouldPersistWriter() {
        Writer writer = new Writer(null, "login1", "password123", "First", "Last");
        Writer saved = writerRepository.save(writer);
        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getLogin(), equalTo("login1"));
    }

    @Test
    void findById_WhenExists_ShouldReturnWriter() {
        Writer writer = new Writer(null, "find@test.com", "pass", "A", "B");
        Writer saved = writerRepository.save(writer);
        Optional<Writer> found = writerRepository.findById(saved.getId());
        assertThat(found.isPresent(), equalTo(true));
        assertThat(found.get().getLogin(), equalTo("find@test.com"));
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Writer> found = writerRepository.findById(99999L);
        assertThat(found.isEmpty(), equalTo(true));
    }

    @Test
    void findAll_WithPageable_ShouldReturnPage() {
        writerRepository.save(new Writer(null, "p1", "pass", "P1", "L1"));
        writerRepository.save(new Writer(null, "p2", "pass", "P2", "L2"));
        var page = writerRepository.findAll(PageRequest.of(0, 1, Sort.by("id")));
        assertThat(page.getContent(), hasSize(1));
        assertThat(page.getTotalElements(), equalTo(2L));
        assertThat(page.getTotalPages(), equalTo(2));
    }

    @Test
    void update_ShouldModifyWriter() {
        Writer saved = writerRepository.save(new Writer(null, "old", "pass", "Old", "Name"));
        Writer updated = saved.withLogin("new").withFirstname("New");
        Writer result = writerRepository.update(updated);
        assertThat(result.getLogin(), equalTo("new"));
        Optional<Writer> found = writerRepository.findById(saved.getId());
        assertThat(found.get().getLogin(), equalTo("new"));
    }

    @Test
    void deleteById_WhenExists_ShouldReturnTrue() {
        Writer saved = writerRepository.save(new Writer(null, "del", "pass", "Del", "User"));
        boolean deleted = writerRepository.deleteById(saved.getId());
        assertThat(deleted, equalTo(true));
        assertThat(writerRepository.findById(saved.getId()).isEmpty(), equalTo(true));
    }

    @Test
    void deleteById_WhenNotExists_ShouldReturnFalse() {
        boolean deleted = writerRepository.deleteById(99999L);
        assertThat(deleted, equalTo(false));
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        Writer saved = writerRepository.save(new Writer(null, "ex", "pass", "Ex", "Ist"));
        assertThat(writerRepository.existsById(saved.getId()), equalTo(true));
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        assertThat(writerRepository.existsById(99999L), equalTo(false));
    }
}
