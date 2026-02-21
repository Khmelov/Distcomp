package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import com.lizaveta.notebook.model.entity.Notice;
import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.model.entity.Writer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class NoticeRepositoryTest extends PostgresTestContainerConfig {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private WriterRepository writerRepository;

    private Long storyId;

    @BeforeEach
    void setUp() {
        Writer writer = writerRepository.save(new Writer(null, "notice-repo@test.com", "password12", "Notice", "Writer"));
        LocalDateTime now = LocalDateTime.now();
        Story story = storyRepository.save(new Story(
                null, writer.getId(), "Notice Story", "Content", now, now, java.util.Set.of()));
        storyId = story.getId();
    }

    @Test
    void save_ShouldPersistNotice() {
        Notice notice = new Notice(null, storyId, "A comment");
        Notice saved = noticeRepository.save(notice);
        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getContent(), equalTo("A comment"));
        assertThat(saved.getStoryId(), equalTo(storyId));
    }

    @Test
    void findById_WhenExists_ShouldReturnNotice() {
        Notice notice = new Notice(null, storyId, "Find me");
        Notice saved = noticeRepository.save(notice);
        Optional<Notice> found = noticeRepository.findById(saved.getId());
        assertThat(found.isPresent(), equalTo(true));
        assertThat(found.get().getContent(), equalTo("Find me"));
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Notice> found = noticeRepository.findById(99999L);
        assertThat(found.isEmpty(), equalTo(true));
    }

    @Test
    void findAll_WithPageable_ShouldReturnPage() {
        noticeRepository.save(new Notice(null, storyId, "n1"));
        noticeRepository.save(new Notice(null, storyId, "n2"));
        var page = noticeRepository.findAll(PageRequest.of(0, 1, Sort.by("id")));
        assertThat(page.getContent(), hasSize(1));
        assertThat(page.getTotalElements(), equalTo(2L));
    }

    @Test
    void findByStoryId_ShouldReturnNoticesForStory() {
        noticeRepository.save(new Notice(null, storyId, "First"));
        noticeRepository.save(new Notice(null, storyId, "Second"));
        List<Notice> byStory = noticeRepository.findByStoryId(storyId);
        assertThat(byStory, hasSize(2));
    }

    @Test
    void update_ShouldModifyNotice() {
        Notice saved = noticeRepository.save(new Notice(null, storyId, "Original"));
        Notice updated = saved.withContent("Updated");
        Notice result = noticeRepository.update(updated);
        assertThat(result.getContent(), equalTo("Updated"));
    }

    @Test
    void deleteById_WhenExists_ShouldReturnTrue() {
        Notice saved = noticeRepository.save(new Notice(null, storyId, "To delete"));
        boolean deleted = noticeRepository.deleteById(saved.getId());
        assertThat(deleted, equalTo(true));
        assertThat(noticeRepository.findById(saved.getId()).isEmpty(), equalTo(true));
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        Notice saved = noticeRepository.save(new Notice(null, storyId, "Exists"));
        assertThat(noticeRepository.existsById(saved.getId()), equalTo(true));
    }
}
