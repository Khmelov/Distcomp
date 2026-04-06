package com.lizaveta.discussion.service;

import com.lizaveta.discussion.cassandra.NoticeByIdRow;
import com.lizaveta.discussion.exception.ResourceNotFoundException;
import com.lizaveta.discussion.exception.ValidationException;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticePersistenceService persistence;

    @Mock
    private NoticeIdGenerator idGenerator;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    void create_ShouldAssignIdAndPersist() {
        when(idGenerator.nextId()).thenReturn(42L);
        NoticeRequestTo req = new NoticeRequestTo(7L, "Hello");
        NoticeResponseTo created = noticeService.create(req);
        assertThat(created.id()).isEqualTo(42L);
        assertThat(created.storyId()).isEqualTo(7L);
        assertThat(created.content()).isEqualTo("Hello");
        verify(persistence).insert(42L, 7L, "Hello");
    }

    @Test
    void findById_WhenMissing_ShouldThrow() {
        when(persistence.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> noticeService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_WhenInvalidId_ShouldThrowValidation() {
        assertThatThrownBy(() -> noticeService.findById(0L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deleteById_WhenMissing_ShouldThrow() {
        when(persistence.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> noticeService.deleteById(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_WhenExists_ShouldDeleteBothTables() {
        NoticeByIdRow row = new NoticeByIdRow();
        row.setId(9L);
        row.setStoryId(3L);
        row.setContent("x");
        when(persistence.findById(9L)).thenReturn(Optional.of(row));
        noticeService.deleteById(9L);
        verify(persistence).delete(9L, 3L);
    }

    @Test
    void update_ShouldCallPersistenceWithOldStory() {
        NoticeByIdRow existing = new NoticeByIdRow();
        existing.setId(5L);
        existing.setStoryId(1L);
        existing.setContent("old");
        when(persistence.findById(5L)).thenReturn(Optional.of(existing));
        NoticeRequestTo req = new NoticeRequestTo(2L, "new");
        NoticeResponseTo out = noticeService.update(5L, req);
        assertThat(out.storyId()).isEqualTo(2L);
        assertThat(out.content()).isEqualTo("new");
        verify(persistence).update(5L, 1L, 2L, "new");
    }

    @Test
    void findAllPaged_ShouldSlice() {
        NoticeByIdRow a = new NoticeByIdRow();
        a.setId(10L);
        a.setStoryId(1L);
        a.setContent("a");
        NoticeByIdRow b = new NoticeByIdRow();
        b.setId(20L);
        b.setStoryId(1L);
        b.setContent("b");
        when(persistence.findAllByIdTable()).thenReturn(List.of(b, a));
        PageResponseTo<NoticeResponseTo> page = noticeService.findAll(0, 1, "id", "asc");
        assertThat(page.content()).hasSize(1);
        assertThat(page.totalElements()).isEqualTo(2);
        assertThat(page.content().get(0).id()).isEqualTo(10L);
    }
}
