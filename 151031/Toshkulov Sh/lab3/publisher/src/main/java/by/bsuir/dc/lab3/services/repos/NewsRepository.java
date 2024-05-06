package by.bsuir.dc.lab3.services.repos;

import by.bsuir.dc.lab3.entities.Editor;
import by.bsuir.dc.lab3.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News,Long> {
    News findByTitle(String title);
}
