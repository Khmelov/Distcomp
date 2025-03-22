package by.kopvzakone.distcomp.repositories;

import by.kopvzakone.distcomp.entities.Editor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Repository
public interface EditorRepository extends Repo<Editor> {

}
