package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private AuthorService authorService;

    @Test
    void createMessage_shouldSave() throws ChangeSetPersister.NotFoundException {
        AuthorRequestTo author = new AuthorRequestTo();
        author.setLogin("msg@mail.com");
        author.setPassword("password123");
        author.setFirstname("Msg");
        author.setLastname("Owner");

        AuthorResponseTo savedAuthor = authorService.create(author);

        IssueRequestTo issue = new IssueRequestTo();
        issue.setAuthorId(savedAuthor.getId());
        issue.setTitle("Issue");
        issue.setContent("Issue content");

        IssueResponseTo savedIssue = issueService.create(issue);

        MessageRequestTo message = new MessageRequestTo();
        message.setIssueId(savedIssue.getId());
        message.setContent("Test message");

        MessageResponseTo savedMessage =
                messageService.create(message);

        assertNotNull(savedMessage.getId());
        assertEquals("Test message", savedMessage.getContent());
    }

    @Test
    void findById_whenNotExists_shouldThrowException() {
        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> messageService.findById(999L));
    }

    @Test
    void deleteMessage_shouldRemoveEntity() throws ChangeSetPersister.NotFoundException {

        AuthorRequestTo author = new AuthorRequestTo();
        author.setLogin("del@mail.com");
        author.setPassword("password123");
        author.setFirstname("Del");
        author.setLastname("Owner");

        AuthorResponseTo savedAuthor = authorService.create(author);

        IssueRequestTo issue = new IssueRequestTo();
        issue.setAuthorId(savedAuthor.getId());
        issue.setTitle("Issue");
        issue.setContent("Content");

        IssueResponseTo savedIssue = issueService.create(issue);

        MessageRequestTo message = new MessageRequestTo();
        message.setIssueId(savedIssue.getId());
        message.setContent("Delete me");

        MessageResponseTo savedMessage =
                messageService.create(message);

        messageService.delete(savedMessage.getId());

        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> messageService.findById(savedMessage.getId()));
    }

    @Test
    void findAll_withPagination_shouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<MessageResponseTo> page =
                messageService.findAll(pageable);

        assertNotNull(page);
    }
}
