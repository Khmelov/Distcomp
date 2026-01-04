package com.blog.repository.impl;

import com.blog.model.Tag;
import com.blog.repository.TagRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTagRepository implements TagRepository {
    private final Map<Long, Tag> tags = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Tag> findAll() {
        return new ArrayList<>(tags.values());
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(tags.get(id));
    }

    @Override
    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            tag.setId(idCounter.getAndIncrement());
        }
        tags.put(tag.getId(), tag);
        return tag;
    }

    @Override
    public Tag update(Tag tag) {
        if (tag.getId() == null || !tags.containsKey(tag.getId())) {
            throw new IllegalArgumentException("Tag not found");
        }
        tags.put(tag.getId(), tag);
        return tag;
    }

    @Override
    public boolean deleteById(Long id) {
        return tags.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return tags.containsKey(id);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tags.values().stream()
                .filter(tag -> tag.getName().equals(name))
                .findFirst();
    }
}