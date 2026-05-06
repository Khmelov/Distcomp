package com.example.discussion.service;

import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import com.example.discussion.exception.ResourceNotFoundException;
import com.example.discussion.model.PostById;
import com.example.discussion.model.PostByStory;
import com.example.discussion.model.PostByStoryKey;
import com.example.discussion.repository.PostByIdRepository;
import com.example.discussion.repository.PostByStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostByIdRepository postByIdRepository;
    private final PostByStoryRepository postByStoryRepository;
    private final PostIdGenerator postIdGenerator;

    public List<PostResponseTo> findAll(int page, int size, String sortDir, String content) {
        List<PostById> all = postByIdRepository.findAll().stream().toList();
        if (content != null && !content.isBlank()) {
            String lowered = content.toLowerCase();
            all = all.stream()
                    .filter(post -> post.getContent() != null && post.getContent().toLowerCase().contains(lowered))
                    .toList();
        }
        Comparator<PostById> comparator = Comparator.comparing(PostById::getId);
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        all = all.stream().sorted(comparator).toList();

        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return all.subList(from, to).stream().map(this::mapToResponse).toList();
    }

    public PostResponseTo findById(Long id) {
        PostById post = postByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));
        return mapToResponse(post);
    }

    public PostResponseTo create(PostRequestTo request) {
        long id = postIdGenerator.nextId();
        Instant now = Instant.now();

        PostById byId = new PostById();
        byId.setId(id);
        byId.setStoryId(request.getStoryId());
        byId.setContent(request.getContent());
        byId.setCreatedAt(now);
        byId.setModifiedAt(now);
        postByIdRepository.save(byId);

        PostByStory byStory = new PostByStory();
        byStory.setKey(new PostByStoryKey(request.getStoryId(), id));
        byStory.setContent(request.getContent());
        byStory.setCreatedAt(now);
        byStory.setModifiedAt(now);
        postByStoryRepository.save(byStory);

        return mapToResponse(byId);
    }

    public PostResponseTo update(Long id, PostRequestTo request) {
        PostById existing = postByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        // remove old projection row when story id changes
        if (!existing.getStoryId().equals(request.getStoryId())) {
            postByStoryRepository.deleteById(new PostByStoryKey(existing.getStoryId(), id));
        }

        existing.setStoryId(request.getStoryId());
        existing.setContent(request.getContent());
        existing.setModifiedAt(Instant.now());
        postByIdRepository.save(existing);

        PostByStory byStory = new PostByStory();
        byStory.setKey(new PostByStoryKey(request.getStoryId(), id));
        byStory.setContent(request.getContent());
        byStory.setCreatedAt(existing.getCreatedAt());
        byStory.setModifiedAt(existing.getModifiedAt());
        postByStoryRepository.save(byStory);

        return mapToResponse(existing);
    }

    public void delete(Long id) {
        PostById existing = postByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));
        postByIdRepository.deleteById(id);
        postByStoryRepository.deleteById(new PostByStoryKey(existing.getStoryId(), id));
    }

    private PostResponseTo mapToResponse(PostById post) {
        PostResponseTo response = new PostResponseTo();
        response.setId(post.getId());
        response.setStoryId(post.getStoryId());
        response.setContent(post.getContent());
        return response;
    }
}
