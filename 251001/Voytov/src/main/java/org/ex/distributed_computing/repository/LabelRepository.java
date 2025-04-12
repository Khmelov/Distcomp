package org.ex.distributed_computing.repository;

import java.util.List;
import org.ex.distributed_computing.model.Label;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends CrudRepository<Label, Long> {

  List<Label> findAll();
}
