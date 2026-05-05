package by.bsuir.task350.publisher.service;

import by.bsuir.task350.publisher.config.CacheNames;
import by.bsuir.task350.publisher.dto.request.TweetRequestTo;
import by.bsuir.task350.publisher.dto.response.TweetResponseTo;
import by.bsuir.task350.publisher.entity.Tweet;
import by.bsuir.task350.publisher.entity.User;
import by.bsuir.task350.publisher.repository.TweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetServiceCacheTest {
    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private UserService userService;
    @Mock
    private TagService tagService;

    private PublisherCacheService cacheService;
    private TweetService tweetService;

    @BeforeEach
    void setUp() {
        cacheService = new PublisherCacheService(new ConcurrentMapCacheManager(CacheNames.TWEETS));
        tweetService = new TweetService(tweetRepository, userService, tagService, cacheService);
    }

    @Test
    void findByIdCachesValueAndSkipsRepositoryOnSecondRead() {
        Tweet tweet = tweet(3L, 7L, "Redis cache", "Tweet content");
        when(tweetRepository.findById(3L)).thenReturn(Optional.of(tweet));

        TweetResponseTo first = tweetService.findById(3L);
        TweetResponseTo second = tweetService.findById(3L);

        assertEquals(first, second);
        assertEquals(first, cacheService.get(CacheNames.TWEETS, 3L, TweetResponseTo.class));
        verify(tweetRepository, times(1)).findById(3L);
    }

    @Test
    void updateRefreshesCachedValue() {
        Tweet stored = tweet(3L, 7L, "Old title", "Old content");
        User user = new User(7L, "login", "password123", "Ivan", "Ivanov");
        when(tweetRepository.existsByTitleAndIdNot("Updated title", 3L)).thenReturn(false);
        when(tweetRepository.findById(3L)).thenReturn(Optional.of(stored));
        when(userService.getUser(7L)).thenReturn(user);
        when(tweetRepository.saveAndFlush(any(Tweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TweetResponseTo response = tweetService.update(new TweetRequestTo(
                3L,
                7L,
                "Updated title",
                "Updated content",
                null,
                null,
                null,
                null
        ));

        assertEquals(response, cacheService.get(CacheNames.TWEETS, 3L, TweetResponseTo.class));
        verify(tweetRepository).saveAndFlush(stored);
    }

    @Test
    void deleteEvictsCachedValue() {
        Tweet stored = tweet(3L, 7L, "Title", "Content");
        cacheService.put(CacheNames.TWEETS, 3L, new TweetResponseTo(3L, 7L, "Title", "Content", "2024-01-01T10:00:00", "2024-01-01T10:00:00"));
        when(tweetRepository.findById(3L)).thenReturn(Optional.of(stored));

        tweetService.delete(3L);

        assertNull(cacheService.get(CacheNames.TWEETS, 3L, TweetResponseTo.class));
        verify(tweetRepository).delete(stored);
        verify(tweetRepository).flush();
    }

    private Tweet tweet(Long id, Long userId, String title, String content) {
        User user = new User(userId, "login" + userId, "password123", "Ivan", "Ivanov");
        Tweet tweet = new Tweet();
        tweet.setId(id);
        tweet.setUser(user);
        tweet.setTitle(title);
        tweet.setContent(content);
        tweet.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));
        tweet.setModified(LocalDateTime.of(2024, 1, 1, 10, 0));
        tweet.setTags(new LinkedHashSet<>());
        return tweet;
    }
}
