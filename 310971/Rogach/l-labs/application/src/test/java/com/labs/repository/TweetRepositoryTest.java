package com.labs.repository;

import com.labs.domain.entity.Tweet;
import com.labs.domain.entity.Writer;
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

class TweetRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private WriterRepository writerRepository;

    private Writer testWriter;

    @BeforeEach
    void setUp() {
        tweetRepository.deleteAll();
        writerRepository.deleteAll();

        testWriter = Writer.builder()
                .login("tweettest@example.com")
                .password("password123")
                .firstname("Tweet")
                .lastname("Writer")
                .build();
        testWriter = writerRepository.save(testWriter);
    }

    @Test
    void testSave() {
        Tweet tweet = Tweet.builder()
                .writer(testWriter)
                .title("Test Tweet")
                .content("This is a test tweet content")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();

        Tweet saved = tweetRepository.save(tweet);

        assertNotNull(saved.getId());
        assertEquals("Test Tweet", saved.getTitle());
        assertEquals(testWriter.getId(), saved.getWriter().getId());
    }

    @Test
    void testFindById() {
        Tweet tweet = Tweet.builder()
                .writer(testWriter)
                .title("Find Tweet")
                .content("Content for find test")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();

        Tweet saved = tweetRepository.save(tweet);
        Tweet found = tweetRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Find Tweet", found.getTitle());
    }

    @Test
    void testFindAllWithPagination() {
        for (int i = 1; i <= 5; i++) {
            tweetRepository.save(Tweet.builder()
                    .writer(testWriter)
                    .title("Tweet " + i)
                    .content("Content " + i)
                    .created(LocalDateTime.now())
                    .modified(LocalDateTime.now())
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<Tweet> page = tweetRepository.findAll(pageable);

        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    @Test
    void testFindAllWithSorting() {
        LocalDateTime now = LocalDateTime.now();
        tweetRepository.save(Tweet.builder()
                .writer(testWriter)
                .title("C Tweet")
                .content("Content C")
                .created(now.minusDays(3))
                .modified(now.minusDays(3))
                .build());

        tweetRepository.save(Tweet.builder()
                .writer(testWriter)
                .title("A Tweet")
                .content("Content A")
                .created(now.minusDays(1))
                .modified(now.minusDays(1))
                .build());

        tweetRepository.save(Tweet.builder()
                .writer(testWriter)
                .title("B Tweet")
                .content("Content B")
                .created(now.minusDays(2))
                .modified(now.minusDays(2))
                .build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Tweet> page = tweetRepository.findAll(pageable);

        List<Tweet> tweets = page.getContent();
        assertEquals("A Tweet", tweets.get(0).getTitle());
        assertEquals("B Tweet", tweets.get(1).getTitle());
        assertEquals("C Tweet", tweets.get(2).getTitle());
    }

    @Test
    void testFindAllWithFiltering() {
        tweetRepository.save(Tweet.builder()
                .writer(testWriter)
                .title("Java Programming")
                .content("Content about Java")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build());

        tweetRepository.save(Tweet.builder()
                .writer(testWriter)
                .title("Python Programming")
                .content("Content about Python")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build());

        Specification<Tweet> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%java%");

        Page<Tweet> page = tweetRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Java Programming", page.getContent().get(0).getTitle());
    }

    @Test
    void testFindWriterByTweetId() {
        Tweet tweet = Tweet.builder()
                .writer(testWriter)
                .title("Test Tweet")
                .content("Content")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();

        Tweet saved = tweetRepository.save(tweet);
        Writer foundWriter = tweetRepository.findWriterByTweetId(saved.getId());

        assertNotNull(foundWriter);
        assertEquals(testWriter.getId(), foundWriter.getId());
    }

    @Test
    void testUpdate() {
        Tweet tweet = Tweet.builder()
                .writer(testWriter)
                .title("Original Title")
                .content("Original Content")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();

        Tweet saved = tweetRepository.save(tweet);
        saved.setTitle("Updated Title");
        saved.setContent("Updated Content");
        saved.setModified(LocalDateTime.now());

        Tweet updated = tweetRepository.save(saved);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Content", updated.getContent());
    }

    @Test
    void testDelete() {
        Tweet tweet = Tweet.builder()
                .writer(testWriter)
                .title("Delete Tweet")
                .content("Content to delete")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();

        Tweet saved = tweetRepository.save(tweet);
        Long id = saved.getId();

        tweetRepository.deleteById(id);

        assertFalse(tweetRepository.findById(id).isPresent());
    }
}

