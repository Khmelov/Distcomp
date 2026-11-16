package by.bsuir.discussion.service;

import by.bsuir.discussion.entity.MessageById;
import by.bsuir.discussion.repository.MessageByIdRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageIdsTest {

    @Test
    @DisplayName("Следующий id продолжается после уже записанного максимума")
    void continuesAfterExistingMax() {
        MessageByIdRepository repository = mock(MessageByIdRepository.class);
        MessageById older = new MessageById();
        older.setId(3L);
        MessageById newer = new MessageById();
        newer.setId(8L);
        when(repository.findAll()).thenReturn(List.of(older, newer));

        MessageIds ids = new MessageIds(repository);
        ids.loadExistingIds();

        assertThat(ids.nextId()).isEqualTo(9L);
        assertThat(ids.nextId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Пустая таблица начинает нумерацию с единицы")
    void startsAtOne() {
        MessageByIdRepository repository = mock(MessageByIdRepository.class);
        when(repository.findAll()).thenReturn(List.of());

        MessageIds ids = new MessageIds(repository);
        ids.loadExistingIds();

        assertThat(ids.nextId()).isEqualTo(1L);
    }
}
