package com.distcomp.discussion.post.repo;

import com.distcomp.discussion.post.domain.Post;
import com.distcomp.discussion.post.domain.PostKey;
import java.util.List;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface PostRepository extends CassandraRepository<Post, PostKey> {
    List<Post> findAllByKeyCountryAndKeyArticleId(String country, long articleId);

    @AllowFiltering
    List<Post> findAllByKeyId(Long id);
}
