package com.lizaveta.discussion.service;

import com.lizaveta.discussion.cassandra.NoticeByIdRow;
import com.lizaveta.discussion.cassandra.NoticeByStoryKey;
import com.lizaveta.discussion.cassandra.NoticeByStoryRow;
import com.lizaveta.discussion.repository.NoticeByIdCassandraRepository;
import com.lizaveta.discussion.repository.NoticeByStoryCassandraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Keeps {@code tbl_notice_by_id} and {@code tbl_notice_by_story} in sync.
 */
@Service
public class NoticePersistenceService {

    private final NoticeByIdCassandraRepository byIdRepository;
    private final NoticeByStoryCassandraRepository byStoryRepository;

    public NoticePersistenceService(
            final NoticeByIdCassandraRepository byIdRepository,
            final NoticeByStoryCassandraRepository byStoryRepository) {
        this.byIdRepository = byIdRepository;
        this.byStoryRepository = byStoryRepository;
    }

    public void insert(final long id, final long storyId, final String content) {
        NoticeByIdRow byId = new NoticeByIdRow();
        byId.setId(id);
        byId.setStoryId(storyId);
        byId.setContent(content);
        byIdRepository.insert(byId);
        NoticeByStoryRow byStory = new NoticeByStoryRow();
        byStory.setKey(new NoticeByStoryKey(storyId, id));
        byStory.setContent(content);
        byStoryRepository.insert(byStory);
    }

    public Optional<NoticeByIdRow> findById(final long id) {
        return byIdRepository.findById(id);
    }

    public List<NoticeByIdRow> findAllByIdTable() {
        return byIdRepository.findAll();
    }

    public List<NoticeByStoryRow> findByStoryId(final long storyId) {
        return byStoryRepository.findByStoryPartition(storyId);
    }

    public void delete(final long id, final long storyId) {
        byIdRepository.deleteById(id);
        byStoryRepository.deleteById(new NoticeByStoryKey(storyId, id));
    }

    public void update(final long id, final long oldStoryId, final long newStoryId, final String content) {
        if (oldStoryId != newStoryId) {
            byStoryRepository.deleteById(new NoticeByStoryKey(oldStoryId, id));
            NoticeByStoryRow moved = new NoticeByStoryRow();
            moved.setKey(new NoticeByStoryKey(newStoryId, id));
            moved.setContent(content);
            byStoryRepository.insert(moved);
        } else {
            NoticeByStoryRow same = new NoticeByStoryRow();
            same.setKey(new NoticeByStoryKey(newStoryId, id));
            same.setContent(content);
            byStoryRepository.insert(same);
        }
        NoticeByIdRow byId = new NoticeByIdRow();
        byId.setId(id);
        byId.setStoryId(newStoryId);
        byId.setContent(content);
        byIdRepository.insert(byId);
    }
}
