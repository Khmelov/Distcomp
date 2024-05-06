package by.bsuir.dc.lab4.services.repos;

import by.bsuir.dc.lab4.entities.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkerRepository extends JpaRepository<Marker,Long> {
    Marker findByName(String name);
}
