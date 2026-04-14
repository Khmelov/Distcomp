package by.bsuir.distcomp.core.repository;
import by.bsuir.distcomp.core.domain.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    boolean existsByName(String name);
    Optional<Marker> findByName(String name); // ДОБАВИТЬ
}