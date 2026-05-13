package org.rv.lab1.discussion.repo;

import org.rv.lab1.discussion.domain.CommentByStory;
import org.rv.lab1.discussion.domain.CommentByStoryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentByStoryRepository extends CassandraRepository<CommentByStory, CommentByStoryKey> {
    List<CommentByStory> findAllByKeyStoryId(Long storyId);
}

