package com.lizaveta.discussion.repository;

import com.lizaveta.discussion.cassandra.NoticeByStoryKey;
import com.lizaveta.discussion.cassandra.NoticeByStoryRow;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface NoticeByStoryCassandraRepository extends CassandraRepository<NoticeByStoryRow, NoticeByStoryKey> {

    @Query("SELECT * FROM tbl_notice_by_story WHERE story_id = ?0")
    List<NoticeByStoryRow> findByStoryPartition(Long storyId);
}
