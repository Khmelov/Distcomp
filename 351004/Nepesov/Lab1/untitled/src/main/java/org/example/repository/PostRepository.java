package org.example.repository;

import org.example.model.Post;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepository extends InMemoryRepository<Post> {}