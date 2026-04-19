package com.lizaveta.discussion.service;

import com.lizaveta.discussion.cassandra.NoticeByIdRow;
import com.lizaveta.discussion.cassandra.NoticeByStoryKey;
import com.lizaveta.discussion.cassandra.NoticeByStoryRow;
import com.lizaveta.discussion.repository.NoticeByIdCassandraRepository;
import com.lizaveta.discussion.repository.NoticeByStoryCassandraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoticePersistenceServiceTest {

    @Mock
    private NoticeByIdCassandraRepository byIdRepository;

    @Mock
    private NoticeByStoryCassandraRepository byStoryRepository;

    @InjectMocks
    private NoticePersistenceService persistenceService;

    @Test
    void insert_ShouldWriteBothTables() {
        persistenceService.insert(1L, 2L, "text");
        ArgumentCaptor<NoticeByIdRow> idCap = ArgumentCaptor.forClass(NoticeByIdRow.class);
        verify(byIdRepository).insert(idCap.capture());
        assertThat(idCap.getValue().getId()).isEqualTo(1L);
        assertThat(idCap.getValue().getStoryId()).isEqualTo(2L);
        assertThat(idCap.getValue().getContent()).isEqualTo("text");
        verify(byStoryRepository).insert(any(NoticeByStoryRow.class));
    }

    @Test
    void delete_ShouldRemoveFromBothTables() {
        persistenceService.delete(9L, 3L);
        verify(byIdRepository).deleteById(9L);
        verify(byStoryRepository).deleteById(new NoticeByStoryKey(3L, 9L));
    }
}
