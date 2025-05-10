package org.ex.distributed_computing.repository;

import java.util.List;
import org.ex.distributed_computing.model.Tweet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends CrudRepository<Tweet, Long> {

  List<Tweet> findAll();

  boolean existsByTitle(String title);
}
