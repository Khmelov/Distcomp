package by.bsuir.romamuhtasarov.api;

import java.util.List;

public interface Service<T, K> {


    List<T> getAll();

    T update(K requestTo);

    T get(long id);

    T delete(long id);

    T add(K requestTo);

}
