    package com.example.discussion.service;

    import com.example.discussion.dto.PostRequestTo;
    import com.example.discussion.dto.PostResponseTo;
    import com.example.discussion.model.Post;
    import com.example.discussion.repository.PostRepository;
    import com.example.discussion.service.mapper.PostMapper;
    import org.mapstruct.factory.Mappers;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    @Transactional
    public class PostService {

        private final PostRepository postRepository;
        private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
        private final SequenceGeneratorService sequenceGenerator;

        public PostService(PostRepository postRepository, SequenceGeneratorService sequenceGenerator) {
            this.postRepository = postRepository;
            this.sequenceGenerator = sequenceGenerator;
        }

        public List<PostResponseTo> findAll() {
            return postRepository.findAll().stream()
                    .map(postMapper::toDto)
                    .collect(Collectors.toList());
        }

        public PostResponseTo findById(Long id) {
            Optional<Post> post = postRepository.findById(id);
            return post.map(postMapper::toDto).orElse(null);
        }


        public PostResponseTo save(PostRequestTo postRequestTo) {
            Post post = postMapper.toEntity(postRequestTo);
            post.setId(sequenceGenerator.generateSequence("post_sequence"));
            Post savedPost = postRepository.save(post);
            return postMapper.toDto(savedPost);
        }

        public PostResponseTo update(PostRequestTo postRequestTo) {
            Post existingPost = postRepository.findById(postRequestTo.getId()).orElseThrow(() -> new RuntimeException("Post not found"));
            postMapper.updateEntityFromDto(postRequestTo, existingPost);
            Post updatedPost = postRepository.save(existingPost);
            return postMapper.toDto(updatedPost);
        }

        public PostResponseTo deleteById(Long id) {
            postRepository.deleteById(id);
            return new PostResponseTo();
        }
    }