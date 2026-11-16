package by.bsuir.discussion.service;

import by.bsuir.discussion.dto.MessageRequestTo;
import by.bsuir.discussion.dto.MessageResponseTo;
import by.bsuir.discussion.entity.MessageById;
import by.bsuir.discussion.entity.MessageByStory;
import by.bsuir.discussion.entity.MessageKey;
import by.bsuir.discussion.exception.ApiException;
import by.bsuir.discussion.repository.MessageByIdRepository;
import by.bsuir.discussion.repository.MessageByStoryRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageByStoryRepository byStory;

    @Mock
    private MessageByIdRepository byId;

    @Mock
    private MessageIds ids;

    private MessageService service;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        service = new MessageService(byStory, byId, ids, new RequestValidator(validator));
    }

    @Test
    @DisplayName("Создание пишет обе таблицы и назначает id на стороне discussion")
    void createWritesBothTables() {
        when(ids.nextId()).thenReturn(4L);

        MessageResponseTo created = service.create(request(null, 10L, "hello"));

        assertThat(created.getId()).isEqualTo(4L);
        assertThat(created.getStoryId()).isEqualTo(10L);
        ArgumentCaptor<MessageByStory> storyRow = ArgumentCaptor.forClass(MessageByStory.class);
        verify(byStory).save(storyRow.capture());
        assertThat(storyRow.getValue().getKey()).isEqualTo(new MessageKey(10L, 4L));
        assertThat(storyRow.getValue().getContent()).isEqualTo("hello");
        ArgumentCaptor<MessageById> idRow = ArgumentCaptor.forClass(MessageById.class);
        verify(byId).save(idRow.capture());
        assertThat(idRow.getValue().getId()).isEqualTo(4L);
        assertThat(idRow.getValue().getStoryId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Короткий текст отклоняется до обращения к Cassandra")
    void rejectsShortContent() {
        assertThatThrownBy(() -> service.create(request(null, 1L, "a")))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(40001);
    }

    @Test
    @DisplayName("Чтение отсутствующего сообщения даёт 404")
    void missingMessage() {
        when(byId.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(9L))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(40401);
    }

    @Test
    @DisplayName("Смена storyId удаляет строку из старой партиции")
    void updateMovesPartition() {
        when(byId.findById(7L)).thenReturn(Optional.of(stored(7L, 1L, "old")));

        MessageResponseTo updated = service.update(7L, request(7L, 2L, "new-text"));

        assertThat(updated.getStoryId()).isEqualTo(2L);
        assertThat(updated.getContent()).isEqualTo("new-text");
        verify(byStory).deleteById(new MessageKey(1L, 7L));
        ArgumentCaptor<MessageByStory> storyRow = ArgumentCaptor.forClass(MessageByStory.class);
        verify(byStory).save(storyRow.capture());
        assertThat(storyRow.getValue().getKey()).isEqualTo(new MessageKey(2L, 7L));
    }

    @Test
    @DisplayName("Обновление внутри той же истории не удаляет партицию")
    void updateKeepsPartition() {
        when(byId.findById(7L)).thenReturn(Optional.of(stored(7L, 1L, "old")));

        service.update(null, request(7L, 1L, "edited"));

        verify(byStory, never()).deleteById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Расхождение id пути и тела даёт 40004")
    void rejectsMismatchedId() {
        assertThatThrownBy(() -> service.update(3L, request(4L, 1L, "ok")))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(40004);
    }

    @Test
    @DisplayName("Список истории сортируется по id")
    void listsStoryMessagesInIdOrder() {
        MessageByStory second = storyRow(1L, 8L, "b");
        MessageByStory first = storyRow(1L, 3L, "a");
        when(byStory.findByStoryId(1L)).thenReturn(List.of(second, first));

        assertThat(service.findByStoryId(1L)).extracting(MessageResponseTo::getId).containsExactly(3L, 8L);
    }

    @Test
    @DisplayName("Удаление стирает обе строки")
    void deleteRemovesBothRows() {
        when(byId.findById(5L)).thenReturn(Optional.of(stored(5L, 2L, "gone")));

        service.delete(5L);

        verify(byStory).deleteById(new MessageKey(2L, 5L));
        verify(byId).deleteById(5L);
    }

    @Test
    @DisplayName("Общий список читается из таблицы по id и сортируется")
    void listsAll() {
        when(byId.findAll()).thenReturn(List.of(stored(8L, 1L, "b"), stored(2L, 1L, "a")));

        assertThat(service.getAll()).extracting(MessageResponseTo::getId).containsExactly(2L, 8L);
    }

    @Test
    @DisplayName("Пустое тело и обновление без id отклоняются")
    void rejectsEmptyUpdate() {
        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(40001);
        assertThatThrownBy(() -> service.update(null, request(null, 1L, "ok")))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(40003);
    }

    private MessageRequestTo request(Long id, Long storyId, String content) {
        MessageRequestTo request = new MessageRequestTo();
        request.setId(id);
        request.setStoryId(storyId);
        request.setContent(content);
        return request;
    }

    private MessageById stored(Long id, Long storyId, String content) {
        MessageById row = new MessageById();
        row.setId(id);
        row.setStoryId(storyId);
        row.setContent(content);
        return row;
    }

    private MessageByStory storyRow(Long storyId, Long id, String content) {
        MessageByStory row = new MessageByStory();
        row.setKey(new MessageKey(storyId, id));
        row.setContent(content);
        return row;
    }
}
