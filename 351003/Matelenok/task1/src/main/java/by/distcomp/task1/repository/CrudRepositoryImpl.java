package by.distcomp.task1.repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
public abstract class CrudRepositoryImpl<T> implements CrudRepository<T, Long> {

    protected final Map<Long, T> storage = new HashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(0);

    protected abstract Long getId(T entity);
    protected abstract void setId(T entity, Long id);
    @Override
    public T save(T entity) {
        Long id = getId(entity);
        if (id == null) {
            id = idGenerator.incrementAndGet();
            setId(entity, id);
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

}
