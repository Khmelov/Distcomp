package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MarkServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MarkService markService;

    @Test
    void createMark_shouldSave() {

        MarkRequestTo request = new MarkRequestTo();
        request.setName("Important");

        MarkResponseTo response = markService.create(request);

        assertNotNull(response.getId());
        assertEquals("Important", response.getName());
    }

    @Test
    void findById_shouldReturnMark() throws ChangeSetPersister.NotFoundException {

        MarkRequestTo request = new MarkRequestTo();
        request.setName("Bug");

        MarkResponseTo saved = markService.create(request);

        MarkResponseTo found = markService.findById(saved.getId());

        assertEquals("Bug", found.getName());
    }

    @Test
    void findById_whenNotExists_shouldThrowException() {
        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> markService.findById(999L));
    }

    @Test
    void updateMark_shouldChangeName() throws ChangeSetPersister.NotFoundException {

        MarkRequestTo request = new MarkRequestTo();
        request.setName("Old");

        MarkResponseTo saved = markService.create(request);

        MarkRequestTo update = new MarkRequestTo();
        update.setName("New");

        MarkResponseTo updated =
                markService.update(saved.getId(), update);

        assertEquals("New", updated.getName());
    }

    @Test
    void deleteMark_shouldRemoveEntity() throws ChangeSetPersister.NotFoundException {

        MarkRequestTo request = new MarkRequestTo();
        request.setName("DeleteMe");

        MarkResponseTo saved = markService.create(request);

        markService.delete(saved.getId());

        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> markService.findById(saved.getId()));
    }

    @Test
    void findAll_withPagination_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<MarkResponseTo> page = markService.findAll(pageable);
        assertNotNull(page);
    }
}
