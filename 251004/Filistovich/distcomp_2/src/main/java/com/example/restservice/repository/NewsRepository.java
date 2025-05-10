package com.example.restservice.repository;

import com.example.restservice.model.News;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    //List<News> getAll();
    News getNewsById(@Positive Long id);
    void deleteNewsById(@Positive Long id);

    boolean existsByTitle(@Size(min=2, max=64) String title);
}
