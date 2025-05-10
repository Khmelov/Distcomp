package org.ex.distributed_computing.repository;

import java.util.List;
import org.ex.distributed_computing.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

  List<Post> findAll();

  @Query(
      value = "SELECT nextval('tbl_post_id_seq')",
      nativeQuery = true
  )
  Long nextId();
}
