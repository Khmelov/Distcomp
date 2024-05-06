package by.bsuir.dc.lab2.services.repos;

import by.bsuir.dc.lab2.entities.Editor;
import by.bsuir.dc.lab2.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News,Long> {
    News findByTitle(String title);
}
