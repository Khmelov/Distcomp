package by.kapinskiy.Task310.repositories.implementations;

import by.kapinskiy.Task310.models.Tag;
import by.kapinskiy.Task310.repositories.TagsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class InMemoryTagsRepository implements TagsRepository {
    private final ConcurrentHashMap<Long, Tag> tags = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();


    @Override
    public void delete(Tag tag) {
        tags.remove(tag.getId());
    }

    @Override
    public Tag save(Tag tag) {
        long id = tag.getId();
        if (!tags.containsKey(id)) {
            id = idGenerator.incrementAndGet();
            tag.setId(id);
        } else {
            Tag prev = tags.get(id);
            prev.getIssues().stream().forEach(issue ->
                    issue.getTags().set(issue.getTags().indexOf(prev), tag)
            );
        }

        tags.put(id, tag);
        return tag;
    }

    @Override
    public void deleteById(long id) {
        Tag tag = tags.get(id);
        tag.getIssues().stream().forEach(issue -> issue.getTags().remove(tag));
        tags.remove(id);
    }

    @Override
    public Optional<Tag> findById(long id) {
        if (tags.containsKey(id)) {
            return Optional.of(tags.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Tag> findAll() {
        return tags.values().stream().toList();
    }

    @Override
    public boolean existsById(long id) {
        return tags.containsKey(id);
    }

    @Override
    public boolean existsByName(String name) {
        return tags.values().stream().anyMatch(tag -> tag.getName().equals(name));
    }
}
