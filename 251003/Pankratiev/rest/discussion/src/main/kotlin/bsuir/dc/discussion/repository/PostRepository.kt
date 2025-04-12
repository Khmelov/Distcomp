package bsuir.dc.discussion.repository

import bsuir.dc.discussion.entity.Post
import bsuir.dc.discussion.entity.PostKey
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CassandraRepository<Post, PostKey> {
}
