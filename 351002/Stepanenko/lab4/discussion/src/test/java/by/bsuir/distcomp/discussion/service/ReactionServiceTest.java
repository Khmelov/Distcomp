package by.bsuir.distcomp.discussion.service;

import by.bsuir.distcomp.discussion.domain.Reaction;
import by.bsuir.distcomp.discussion.domain.ReactionById;
import by.bsuir.distcomp.discussion.domain.ReactionKey;
import by.bsuir.distcomp.discussion.exception.ResourceNotFoundException;
import by.bsuir.distcomp.discussion.repository.ReactionByIdRepository;
import by.bsuir.distcomp.discussion.repository.ReactionRepository;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private ReactionByIdRepository reactionByIdRepository;

    @Mock
    private ReactionIdGenerator idGenerator;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void create_persistsMainAndLookup() {
        when(idGenerator.nextId()).thenReturn(100L);

        ReactionRequestTo req = new ReactionRequestTo();
        req.setTweetId(7L);
        req.setContent("hello");

        ReactionResponseTo res = reactionService.create(req);

        assertThat(res.getId()).isEqualTo(100L);
        assertThat(res.getTweetId()).isEqualTo(7L);
        assertThat(res.getContent()).isEqualTo("hello");

        verify(reactionRepository).save(any(Reaction.class));
        verify(reactionByIdRepository).save(any(ReactionById.class));
    }

    @Test
    void getById_returnsFromLookup() {
        ReactionById lookup = new ReactionById();
        lookup.setId(1L);
        lookup.setTweetId(2L);
        lookup.setContent("x");
        when(reactionByIdRepository.findById(1L)).thenReturn(Optional.of(lookup));

        ReactionResponseTo res = reactionService.getById(1L);

        assertThat(res.getTweetId()).isEqualTo(2L);
        assertThat(res.getContent()).isEqualTo("x");
    }

    @Test
    void getById_notFound_throws() {
        when(reactionByIdRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reactionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_removesBoth() {
        ReactionById lookup = new ReactionById();
        lookup.setId(5L);
        lookup.setTweetId(3L);
        lookup.setContent("c");
        when(reactionByIdRepository.findById(5L)).thenReturn(Optional.of(lookup));

        reactionService.deleteById(5L);

        ArgumentCaptor<ReactionKey> keyCaptor = ArgumentCaptor.forClass(ReactionKey.class);
        verify(reactionRepository).deleteById(keyCaptor.capture());
        assertThat(keyCaptor.getValue().getTweetId()).isEqualTo(3L);
        assertThat(keyCaptor.getValue().getId()).isEqualTo(5L);
        verify(reactionByIdRepository).deleteById(5L);
    }

    @Test
    void getByTweetId_mapsRows() {
        ReactionKey key = new ReactionKey();
        key.setTweetId(10L);
        key.setId(20L);
        Reaction row = new Reaction();
        row.setKey(key);
        row.setContent("z");
        when(reactionRepository.findAllByTweetId(10L)).thenReturn(List.of(row));

        List<ReactionResponseTo> list = reactionService.getByTweetId(10L);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(20L);
        assertThat(list.get(0).getTweetId()).isEqualTo(10L);
    }

    @Test
    void update_requiresId() {
        ReactionRequestTo req = new ReactionRequestTo();
        req.setTweetId(1L);
        req.setContent("a");
        assertThatThrownBy(() -> reactionService.update(req))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
