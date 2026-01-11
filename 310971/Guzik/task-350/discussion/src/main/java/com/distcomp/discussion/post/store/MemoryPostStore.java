package com.distcomp.discussion.post.store;

import com.distcomp.discussion.post.dto.PostRequest;
import com.distcomp.discussion.post.dto.PostResponse;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!cassandra")
public class MemoryPostStore implements PostStore {

    private static final String DEFAULT_COUNTRY = "by";

    private final AtomicLong idSequence = new AtomicLong(0);

    private final ConcurrentMap<Long, PostResponse> posts = new ConcurrentHashMap<>();

    @Override
    public PostResponse create(PostRequest request) {
        Long id = request.getId() != null ? request.getId() : idSequence.incrementAndGet();
        String country = request.getCountry() != null ? request.getCountry() : DEFAULT_COUNTRY;

        PostResponse saved = new PostResponse(country, request.getArticleId(), id, request.getContent());
        posts.put(id, saved);
        return saved;
    }

    @Override
    public List<PostResponse> listAll() {
        return posts.values().stream()
                .sorted(Comparator.comparing(PostResponse::getId))
                .toList();
    }

    @Override
    public Optional<PostResponse> getById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public Optional<PostResponse> updateById(Long id, PostRequest request) {
        PostResponse existing = posts.get(id);
        if (existing == null) {
            return Optional.empty();
        }

        String country = request.getCountry() != null ? request.getCountry() : existing.getCountry();
        PostResponse updated = new PostResponse(country, request.getArticleId(), id, request.getContent());
        posts.put(id, updated);
        return Optional.of(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        return posts.remove(id) != null;
    }

    @Override
    public List<PostResponse> listByArticle(String country, long articleId) {
        return posts.values().stream()
                .filter(p -> p.getArticleId() == articleId && (country == null || country.equals(p.getCountry())))
                .toList();
    }
}
