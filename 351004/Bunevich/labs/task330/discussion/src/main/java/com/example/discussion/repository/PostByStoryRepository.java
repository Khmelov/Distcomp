package com.example.discussion.repository;

import com.example.discussion.model.PostByStory;
import com.example.discussion.model.PostByStoryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface PostByStoryRepository extends CassandraRepository<PostByStory, PostByStoryKey> {
    @Query("SELECT * FROM tbl_post_by_story WHERE story_id = ?0")
    List<PostByStory> findByStoryId(Long storyId);
}
