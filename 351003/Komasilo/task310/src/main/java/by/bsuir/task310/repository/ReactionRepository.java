package by.bsuir.task310.repository;

import by.bsuir.task310.model.Reaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReactionRepository {
    private final ConcurrentHashMap<Long, Reaction> reactions = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Reaction save(Reaction reaction) {
        long id = idGenerator.incrementAndGet();
        reaction.setId(id);
        reactions.put(id, reaction);
        return reaction;
    }

    public List<Reaction> findAll() {
        return new ArrayList<>(reactions.values());
    }

    public Optional<Reaction> findById(Long id) {
        return Optional.ofNullable(reactions.get(id));
    }

    public Reaction update(Reaction reaction) {
        reactions.put(reaction.getId(), reaction);
        return reaction;
    }

    public boolean deleteById(Long id) {
        return reactions.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return reactions.containsKey(id);
    }
}