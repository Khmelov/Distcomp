package com.lizaveta.notebook.client;

import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test double for the discussion service (no Docker / no second JVM).
 */
public class InMemoryDiscussionNoticeClient implements DiscussionNoticeClient {

    private final Map<Long, NoticeResponseTo> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(10_000);

    public void clear() {
        store.clear();
    }

    @Override
    public NoticeResponseTo create(final NoticeRequestTo request) {
        long id = idSequence.incrementAndGet();
        NoticeResponseTo saved = new NoticeResponseTo(id, request.storyId(), request.content());
        store.put(id, saved);
        return saved;
    }

    @Override
    public List<NoticeResponseTo> findAllAsList() {
        List<NoticeResponseTo> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparingLong(NoticeResponseTo::id));
        return all;
    }

    @Override
    public PageResponseTo<NoticeResponseTo> findAllPaged(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        List<NoticeResponseTo> all = new ArrayList<>(findAllAsList());
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        List<NoticeResponseTo> slice = all.subList(from, to);
        int totalPages = safeSize > 0 ? (int) Math.ceil((double) all.size() / safeSize) : 0;
        return new PageResponseTo<>(slice, all.size(), totalPages, safeSize, safePage);
    }

    @Override
    public NoticeResponseTo findById(final Long id) {
        NoticeResponseTo n = store.get(id);
        if (n == null) {
            throw new ResourceNotFoundException("Notice not found with id: " + id);
        }
        return n;
    }

    @Override
    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        return store.values().stream()
                .filter(x -> x.storyId().equals(storyId))
                .sorted(Comparator.comparingLong(NoticeResponseTo::id))
                .toList();
    }

    @Override
    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        if (!store.containsKey(id)) {
            throw new ResourceNotFoundException("Notice not found with id: " + id);
        }
        NoticeResponseTo updated = new NoticeResponseTo(id, request.storyId(), request.content());
        store.put(id, updated);
        return updated;
    }

    @Override
    public void deleteById(final Long id) {
        if (store.remove(id) == null) {
            throw new ResourceNotFoundException("Notice not found with id: " + id);
        }
    }
}
