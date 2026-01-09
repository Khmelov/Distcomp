package org.example.task310rest.repository;

import org.example.task310rest.model.Writer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WriterRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WriterRepository writerRepository;

    @Test
    void testSaveAndFindById() {
        Writer writer = new Writer();
        writer.setLogin("test@example.com");
        writer.setPassword("password123");
        writer.setFirstname("Test");
        writer.setLastname("User");

        Writer saved = writerRepository.save(writer);
        assertNotNull(saved.getId());

        Optional<Writer> found = writerRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getLogin());
    }

    @Test
    void testFindByLogin() {
        Writer writer = new Writer();
        writer.setLogin("unique@example.com");
        writer.setPassword("password123");
        writer.setFirstname("Test");
        writer.setLastname("User");
        writerRepository.save(writer);

        Optional<Writer> found = writerRepository.findByLogin("unique@example.com");
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getLogin());
    }

    @Test
    void testFindAll() {
        Writer writer1 = new Writer();
        writer1.setLogin("user1@example.com");
        writer1.setPassword("password123");
        writer1.setFirstname("User1");
        writer1.setLastname("Test");
        writerRepository.save(writer1);

        Writer writer2 = new Writer();
        writer2.setLogin("user2@example.com");
        writer2.setPassword("password123");
        writer2.setFirstname("User2");
        writer2.setLastname("Test");
        writerRepository.save(writer2);

        List<Writer> all = writerRepository.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void testDelete() {
        Writer writer = new Writer();
        writer.setLogin("delete@example.com");
        writer.setPassword("password123");
        writer.setFirstname("Delete");
        writer.setLastname("Test");
        Writer saved = writerRepository.save(writer);

        writerRepository.deleteById(saved.getId());
        assertFalse(writerRepository.existsById(saved.getId()));
    }
}

