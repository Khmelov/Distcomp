package by.bsuir.discussion.repository;

import by.bsuir.discussion.entity.MessageByStory;
import by.bsuir.discussion.entity.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface MessageByStoryRepository extends CassandraRepository<MessageByStory, MessageKey> {

    @Query("SELECT * FROM tbl_message WHERE story_id = ?0")
    List<MessageByStory> findByStoryId(Long storyId);
}
