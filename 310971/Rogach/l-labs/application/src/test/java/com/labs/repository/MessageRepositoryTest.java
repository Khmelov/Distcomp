package com.labs.repository;

import com.labs.domain.entity.Message;
import com.labs.domain.entity.Tweet;
import com.labs.domain.entity.Writer;
import com.labs.domain.repository.MessageRepository;
import com.labs.domain.repository.TweetRepository;
import com.labs.domain.repository.WriterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private WriterRepository writerRepository;

    private Tweet testTweet;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        tweetRepository.deleteAll();
        writerRepository.deleteAll();

        Writer writer = Writer.builder()
                .login("messagetest@example.com")
                .password("password123")
                .firstname("Message")
                .lastname("Writer")
                .build();
        writer = writerRepository.save(writer);

        testTweet = Tweet.builder()
                .writer(writer)
                .title("Test Tweet")
                .content("Tweet content for messages")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();
        testTweet = tweetRepository.save(testTweet);
    }

    @Test
    void testSave() {
        Message message = Message.builder()
                .tweet(testTweet)
                .content("This is a test message")
                .build();

        Message saved = messageRepository.save(message);

        assertNotNull(saved.getId());
        assertEquals("This is a test message", saved.getContent());
        assertEquals(testTweet.getId(), saved.getTweet().getId());
    }

    @Test
    void testFindById() {
        Message message = Message.builder()
                .tweet(testTweet)
                .content("Find message content")
                .build();

        Message saved = messageRepository.save(message);
        Message found = messageRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Find message content", found.getContent());
    }

    @Test
    void testFindAllWithPagination() {
        for (int i = 1; i <= 5; i++) {
            messageRepository.save(Message.builder()
                    .tweet(testTweet)
                    .content("Message " + i)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<Message> page = messageRepository.findAll(pageable);

        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void testFindAllWithSorting() {
        messageRepository.save(Message.builder()
                .tweet(testTweet)
                .content("C Message")
                .build());

        messageRepository.save(Message.builder()
                .tweet(testTweet)
                .content("A Message")
                .build());

        messageRepository.save(Message.builder()
                .tweet(testTweet)
                .content("B Message")
                .build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "content"));
        Page<Message> page = messageRepository.findAll(pageable);

        List<Message> messages = page.getContent();
        assertEquals("A Message", messages.get(0).getContent());
        assertEquals("B Message", messages.get(1).getContent());
        assertEquals("C Message", messages.get(2).getContent());
    }

    @Test
    void testFindAllWithFiltering() {
        messageRepository.save(Message.builder()
                .tweet(testTweet)
                .content("Java is great")
                .build());

        messageRepository.save(Message.builder()
                .tweet(testTweet)
                .content("Python is awesome")
                .build());

        messageRepository.save(Message.builder()
                .tweet(testTweet)
                .content("Java programming tips")
                .build());

        Specification<Message> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("content")), "%java%");

        Page<Message> page = messageRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .anyMatch(m -> m.getContent().contains("Java")));
    }

    @Test
    void testUpdate() {
        Message message = Message.builder()
                .tweet(testTweet)
                .content("Original content")
                .build();

        Message saved = messageRepository.save(message);
        saved.setContent("Updated content");

        Message updated = messageRepository.save(saved);

        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void testDelete() {
        Message message = Message.builder()
                .tweet(testTweet)
                .content("Delete me")
                .build();

        Message saved = messageRepository.save(message);
        Long id = saved.getId();

        messageRepository.deleteById(id);

        assertFalse(messageRepository.findById(id).isPresent());
    }
}

