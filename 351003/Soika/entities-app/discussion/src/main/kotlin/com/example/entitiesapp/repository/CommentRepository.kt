package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Comment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository : CassandraRepository<Comment, Long> {

    @Query("SELECT * FROM tbl_comment WHERE id = ?0")
    fun findByCommentId(id: Long): Optional<Comment>

    @Query("DELETE FROM tbl_comment WHERE story_id = ?0")
    fun deleteAllByStoryId(storyId: Long)

    @Query("DELETE FROM tbl_comment WHERE story_id = ?0 AND id = ?1")
    fun deleteByCompositeKey(storyId: Long, id: Long)
}