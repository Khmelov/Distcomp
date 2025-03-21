package org.ex.distributed_computing.repository;

import java.util.List;
import org.ex.distributed_computing.model.News;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends CrudRepository<News, Long> {

  List<News> findAll();

  boolean existsByTitle(String title);
}
