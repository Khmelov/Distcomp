package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class IssueServiceTest {

    @Autowired
    private IssueService issueService;

    @Autowired
    private AuthorService authorService;

    @Test
    void createIssue_shouldSave() throws ChangeSetPersister.NotFoundException {

        AuthorRequestTo author = new AuthorRequestTo();
        author.setLogin("serv@mail.com");
        author.setPassword("password123");
        author.setFirstname("Service");
        author.setLastname("Owner");

        AuthorResponseTo savedAuthor = authorService.create(author);

        IssueRequestTo issue = new IssueRequestTo();
        issue.setAuthorId(savedAuthor.getId());
        issue.setTitle("Service Issue");
        issue.setContent("Service Content");

        IssueResponseTo savedIssue = issueService.create(issue);

        assertNotNull(savedIssue.getId());
        assertEquals("Service Issue", savedIssue.getTitle());
    }
}