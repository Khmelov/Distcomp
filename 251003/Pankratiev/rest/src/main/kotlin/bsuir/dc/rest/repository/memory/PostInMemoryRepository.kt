package bsuir.dc.rest.repository.memory

import bsuir.dc.rest.entity.Post
import org.springframework.stereotype.Repository

@Repository
class PostInMemoryRepository: InMemoryRepository<Post>()