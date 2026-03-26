package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import com.lizaveta.notebook.model.entity.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class StoryRepositoryTest extends PostgresTestContainerConfig {

    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private MarkerRepository markerRepository;

    private Long writerId;
    private Long markerId;

    @BeforeEach
    void setUp() {
        var writer = writerRepository.save(new com.lizaveta.notebook.model.entity.Writer(
                null, "story-repo@test.com", "password12", "Story", "Writer"));
        writerId = writer.getId();
        var marker = markerRepository.save(new com.lizaveta.notebook.model.entity.Marker(null, "tag-repo"));
        markerId = marker.getId();
    }

    @Test
    void save_ShouldPersistStory() {
        LocalDateTime now = LocalDateTime.now();
        Story story = new Story(null, writerId, "Title", "Content", now, now, Set.of(markerId));
        Story saved = storyRepository.save(story);
        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getTitle(), equalTo("Title"));
        assertThat(saved.getMarkerIds(), hasSize(1));
    }

    @Test
    void findById_WhenExists_ShouldReturnStory() {
        LocalDateTime now = LocalDateTime.now();
        Story story = new Story(null, writerId, "Find", "Body", now, now, Set.of());
        Story saved = storyRepository.save(story);
        Optional<Story> found = storyRepository.findById(saved.getId());
        assertThat(found.isPresent(), equalTo(true));
        assertThat(found.get().getTitle(), equalTo("Find"));
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Story> found = storyRepository.findById(99999L);
        assertThat(found.isEmpty(), equalTo(true));
    }

    @Test
    void findAll_WithPageable_ShouldReturnPage() {
        LocalDateTime now = LocalDateTime.now();
        storyRepository.save(new Story(null, writerId, "S1", "C1", now, now, Set.of()));
        storyRepository.save(new Story(null, writerId, "S2", "C2", now, now, Set.of()));
        var page = storyRepository.findAll(PageRequest.of(0, 1, Sort.by("id")));
        assertThat(page.getContent(), hasSize(1));
        assertThat(page.getTotalElements(), equalTo(2L));
    }

    @Test
    void findByMarkerId_ShouldReturnStoriesWithMarker() {
        LocalDateTime now = LocalDateTime.now();
        Story withMarker = new Story(null, writerId, "With", "Content", now, now, Set.of(markerId));
        storyRepository.save(withMarker);
        storyRepository.save(new Story(null, writerId, "Without", "Content", now, now, Set.of()));
        List<Story> byMarker = storyRepository.findByMarkerId(markerId);
        assertThat(byMarker, hasSize(1));
        assertThat(byMarker.get(0).getTitle(), equalTo("With"));
    }

    @Test
    void update_ShouldModifyStory() {
        LocalDateTime now = LocalDateTime.now();
        Story saved = storyRepository.save(new Story(null, writerId, "Old", "Content", now, now, Set.of()));
        Story updated = saved.withTitle("New").withContent("New content").withModified(LocalDateTime.now());
        Story result = storyRepository.update(updated);
        assertThat(result.getTitle(), equalTo("New"));
    }

    @Test
    void deleteById_WhenExists_ShouldReturnTrue() {
        LocalDateTime now = LocalDateTime.now();
        Story saved = storyRepository.save(new Story(null, writerId, "Del", "Content", now, now, Set.of()));
        boolean deleted = storyRepository.deleteById(saved.getId());
        assertThat(deleted, equalTo(true));
        assertThat(storyRepository.findById(saved.getId()).isEmpty(), equalTo(true));
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        LocalDateTime now = LocalDateTime.now();
        Story saved = storyRepository.save(new Story(null, writerId, "Ex", "Content", now, now, Set.of()));
        assertThat(storyRepository.existsById(saved.getId()), equalTo(true));
    }
}
