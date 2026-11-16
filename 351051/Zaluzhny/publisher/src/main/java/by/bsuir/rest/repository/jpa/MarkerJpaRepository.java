package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MarkerJpaRepository extends JpaRepository<Marker, Long>, JpaSpecificationExecutor<Marker> {
}
