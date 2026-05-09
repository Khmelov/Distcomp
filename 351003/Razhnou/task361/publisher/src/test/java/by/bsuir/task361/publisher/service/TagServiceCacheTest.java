package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.config.CacheNames;
import by.bsuir.task361.publisher.dto.request.TagRequestTo;
import by.bsuir.task361.publisher.dto.response.TagResponseTo;
import by.bsuir.task361.publisher.entity.Tag;
import by.bsuir.task361.publisher.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceCacheTest {
    @Mock
    private TagRepository tagRepository;

    private PublisherCacheService cacheService;
    private TagService tagService;

    @BeforeEach
    void setUp() {
        cacheService = new PublisherCacheService(new ConcurrentMapCacheManager(CacheNames.TAGS));
        tagService = new TagService(tagRepository, cacheService);
    }

    @Test
    void findByIdCachesValueAndSkipsRepositoryOnSecondRead() {
        Tag tag = new Tag(2L, "redis");
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));

        TagResponseTo first = tagService.findById(2L);
        TagResponseTo second = tagService.findById(2L);

        assertEquals(first, second);
        assertEquals(first, cacheService.get(CacheNames.TAGS, 2L, TagResponseTo.class));
        verify(tagRepository, times(1)).findById(2L);
    }

    @Test
    void updateRefreshesCachedValue() {
        Tag stored = new Tag(2L, "java");
        when(tagRepository.findById(2L)).thenReturn(Optional.of(stored));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TagResponseTo response = tagService.update(new TagRequestTo(2L, "spring"));

        assertEquals(response, cacheService.get(CacheNames.TAGS, 2L, TagResponseTo.class));
        verify(tagRepository).save(stored);
    }

    @Test
    void deleteEvictsCachedValue() {
        Tag stored = new Tag(2L, "spring");
        cacheService.put(CacheNames.TAGS, 2L, new TagResponseTo(2L, "spring"));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(stored));

        tagService.delete(2L);

        assertNull(cacheService.get(CacheNames.TAGS, 2L, TagResponseTo.class));
        verify(tagRepository).delete(stored);
    }
}
