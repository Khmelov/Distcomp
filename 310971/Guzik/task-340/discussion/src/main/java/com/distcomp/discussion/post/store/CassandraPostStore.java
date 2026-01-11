package com.distcomp.discussion.post.store;

import com.distcomp.discussion.post.domain.Post;
import com.distcomp.discussion.post.domain.PostKey;
import com.distcomp.discussion.post.dto.PostRequest;
import com.distcomp.discussion.post.dto.PostResponse;
import com.distcomp.discussion.post.repo.PostRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cassandra")
public class CassandraPostStore implements PostStore {

    private static final String DEFAULT_COUNTRY = "by";

    private final AtomicLong idSequence = new AtomicLong(0);

    private final PostRepository repository;

    public CassandraPostStore(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public PostResponse create(PostRequest request) {
        Long id = request.getId() != null ? request.getId() : idSequence.incrementAndGet();
        String country = request.getCountry() != null ? request.getCountry() : DEFAULT_COUNTRY;
        PostKey key = new PostKey(country, request.getArticleId(), id);
        Post saved = repository.save(new Post(key, request.getContent()));
        return toResponse(saved);
    }

    @Override
    public List<PostResponse> listAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(PostResponse::getId))
                .toList();
    }

    @Override
    public Optional<PostResponse> getById(Long id) {
        return repository.findAllByKeyId(id).stream().findFirst().map(this::toResponse);
    }

    @Override
    public Optional<PostResponse> updateById(Long id, PostRequest request) {
        List<Post> existing = new ArrayList<>(repository.findAllByKeyId(id));
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Post old = existing.get(0);
        String country = request.getCountry() != null ? request.getCountry() : old.getKey().getCountry();
        long articleId = request.getArticleId();
        PostKey newKey = new PostKey(country, articleId, id);

        if (!old.getKey().equals(newKey)) {
            repository.delete(old);
        }

        Post saved = repository.save(new Post(newKey, request.getContent()));
        return Optional.of(toResponse(saved));
    }

    @Override
    public boolean deleteById(Long id) {
        List<Post> existing = repository.findAllByKeyId(id);
        if (existing.isEmpty()) {
            return false;
        }
        repository.deleteAll(existing);
        return true;
    }

    @Override
    public List<PostResponse> listByArticle(String country, long articleId) {
        return repository.findAllByKeyCountryAndKeyArticleId(country, articleId).stream().map(this::toResponse).toList();
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getKey().getCountry(),
                post.getKey().getArticleId(),
                post.getKey().getId(),
                post.getContent()
        );
    }
}
