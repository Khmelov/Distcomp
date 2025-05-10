package org.ex.discussion.repository

import org.ex.discussion.model.Post
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : CrudRepository<Post, Long> {

    @Query(
        value = "SELECT * FROM post WHERE id = :id ALLOW FILTERING;",
        allowFiltering = true
    )
    override fun findById(@Param("id") id: Long): Optional<Post>

    @Query(
        value = "DELETE FROM post WHERE id = :id AND country = :country;"
    )
    fun delete(@Param("id") id: Long, @Param("country") country: String)
}