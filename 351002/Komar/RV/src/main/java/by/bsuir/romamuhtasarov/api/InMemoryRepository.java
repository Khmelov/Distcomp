package by.bsuir.romamuhtasarov.api;

import java.util.List;

public interface InMemoryRepository<T> {

    T get(long id);

    List<T> getAll();

    T delete(long id);

    T insert(T insertObject);

    boolean update(T updatingValue);
}
