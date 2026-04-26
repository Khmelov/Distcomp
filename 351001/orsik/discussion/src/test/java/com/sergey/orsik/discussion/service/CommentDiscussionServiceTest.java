package com.sergey.orsik.discussion.service;

import com.sergey.orsik.discussion.cassandra.CommentByIdRow;
import com.sergey.orsik.discussion.cassandra.CommentByTweetKey;
import com.sergey.orsik.discussion.cassandra.CommentByTweetRow;
import com.sergey.orsik.discussion.client.PublisherTweetClient;
import com.sergey.orsik.discussion.exception.EntityNotFoundException;
import com.sergey.orsik.discussion.repository.CommentByIdRepository;
import com.sergey.orsik.discussion.repository.CommentByTweetRepository;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentDiscussionServiceTest {

    @Mock
    private CommentByIdRepository commentByIdRepository;
    @Mock
    private CommentByTweetRepository commentByTweetRepository;
    @Mock
    private PublisherTweetClient publisherTweetClient;
    @Mock
    private CassandraTemplate cassandraTemplate;

    private CommentDiscussionService service;

    @BeforeEach
    void setUp() {
        service = new CommentDiscussionService(
                commentByIdRepository, commentByTweetRepository, publisherTweetClient, cassandraTemplate, "distcomp");
    }

    @Test
    void createGeneratesIdAndSavesBothProjections() {
        when(commentByIdRepository.existsById(anyLong())).thenReturn(false);
        CommentRequestTo req = new CommentRequestTo(null, 9L, "Hello world", null);

        CommentResponseTo created = service.create(req);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTweetId()).isEqualTo(9L);
        assertThat(created.getContent()).isEqualTo("Hello world");
        assertThat(created.getCreated()).isNotNull();

        verify(publisherTweetClient).requireTweetExists(9L);
        ArgumentCaptor<CommentByIdRow> idCap = ArgumentCaptor.forClass(CommentByIdRow.class);
        ArgumentCaptor<CommentByTweetRow> twCap = ArgumentCaptor.forClass(CommentByTweetRow.class);
        verify(commentByIdRepository).save(idCap.capture());
        verify(commentByTweetRepository).save(twCap.capture());
        assertThat(idCap.getValue().getTweetId()).isEqualTo(9L);
        assertThat(twCap.getValue().getKey().getTweetId()).isEqualTo(9L);
        assertThat(twCap.getValue().getKey().getId()).isEqualTo(created.getId());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(commentByIdRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAllByTweetFiltersContent() {
        Instant t = Instant.parse("2020-01-01T00:00:00Z");
        CommentByTweetRow a = new CommentByTweetRow(new CommentByTweetKey(1L, t, 10L), "Alpha beta");
        CommentByTweetRow b = new CommentByTweetRow(new CommentByTweetKey(1L, t.plusSeconds(1), 11L), "Gamma");
        when(commentByTweetRepository.findByKeyTweetId(1L)).thenReturn(List.of(a, b));

        List<CommentResponseTo> out = service.findAll(0, 10, "id", "asc", 1L, "alp");

        assertThat(out).hasSize(1);
        assertThat(out.getFirst().getContent()).containsIgnoringCase("alp");
    }

    @Test
    void findAllWithoutTweetIdDoesNotScanByIdTable() {
        List<CommentResponseTo> out = service.findAll(0, 10, "id", "asc", null, null);
        assertThat(out).isEmpty();
        verify(commentByIdRepository, never()).findAll();
    }

    @Test
    void updateDeletesOldTweetProjection() {
        CommentByIdRow existing = new CommentByIdRow(5L, 1L, "old", Instant.parse("2021-05-05T12:00:00Z"));
        when(commentByIdRepository.findById(5L)).thenReturn(Optional.of(existing));

        CommentRequestTo req = new CommentRequestTo(5L, 2L, "new text", null);
        service.update(5L, req);

        verify(publisherTweetClient).requireTweetExists(2L);
        verify(commentByTweetRepository).deleteById(new CommentByTweetKey(1L, existing.getCreated(), 5L));
        verify(commentByIdRepository).save(any(CommentByIdRow.class));
        verify(commentByTweetRepository).save(any(CommentByTweetRow.class));
    }

    @Test
    void deleteByIdRemovesBothProjections() {
        CommentByIdRow existing = new CommentByIdRow(7L, 3L, "x", Instant.parse("2022-02-02T00:00:00Z"));
        when(commentByIdRepository.findById(7L)).thenReturn(Optional.of(existing));

        service.deleteById(7L);

        verify(commentByTweetRepository).deleteById(new CommentByTweetKey(3L, existing.getCreated(), 7L));
        verify(commentByIdRepository).deleteById(7L);
    }

    @Test
    void deleteAllByTweetIdDeletesRows() {
        Instant t = Instant.parse("2023-03-03T00:00:00Z");
        CommentByTweetRow r1 = new CommentByTweetRow(new CommentByTweetKey(99L, t, 1L), "a");
        CommentByTweetRow r2 = new CommentByTweetRow(new CommentByTweetKey(99L, t.plusSeconds(1), 2L), "b");
        when(commentByTweetRepository.findByKeyTweetId(99L)).thenReturn(List.of(r1, r2));

        service.deleteAllByTweetId(99L);

        verify(commentByIdRepository).deleteById(1L);
        verify(commentByIdRepository).deleteById(2L);
        verify(cassandraTemplate).execute(any(com.datastax.oss.driver.api.core.cql.SimpleStatement.class));
        verify(commentByTweetRepository, never()).deleteAll(any());
    }

    @Test
    void deleteAllByTweetIdNoOpWhenPartitionEmpty() {
        when(commentByTweetRepository.findByKeyTweetId(42L)).thenReturn(List.of());

        service.deleteAllByTweetId(42L);

        verify(commentByIdRepository, never()).deleteById(anyLong());
        verify(cassandraTemplate, never()).execute(any(com.datastax.oss.driver.api.core.cql.SimpleStatement.class));
    }

    @Test
    void createDoesNotCallPublisherWhenTweetInvalidHandledByClient() {
        doThrow(new EntityNotFoundException("Tweet", 404L))
                .when(publisherTweetClient).requireTweetExists(404L);

        assertThatThrownBy(() -> service.create(new CommentRequestTo(null, 404L, "xx", null)))
                .isInstanceOf(EntityNotFoundException.class);
        verify(commentByIdRepository, never()).save(any());
    }
}
