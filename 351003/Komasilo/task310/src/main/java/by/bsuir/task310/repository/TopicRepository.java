package by.bsuir.task310.repository;

import by.bsuir.task310.model.Topic;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TopicRepository {
    private final ConcurrentHashMap<Long, Topic> topics = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Topic save(Topic topic) {
        long id = idGenerator.incrementAndGet();
        topic.setId(id);
        topics.put(id, topic);
        return topic;
    }

    public List<Topic> findAll() {
        return new ArrayList<>(topics.values());
    }

    public Optional<Topic> findById(Long id) {
        return Optional.ofNullable(topics.get(id));
    }

    public Topic update(Topic topic) {
        topics.put(topic.getId(), topic);
        return topic;
    }

    public boolean deleteById(Long id) {
        return topics.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return topics.containsKey(id);
    }
}