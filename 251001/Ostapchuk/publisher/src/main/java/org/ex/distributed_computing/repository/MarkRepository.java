package org.ex.distributed_computing.repository;

import java.util.List;
import org.ex.distributed_computing.model.Mark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkRepository extends CrudRepository<Mark, Long> {

  List<Mark> findAll();
}
