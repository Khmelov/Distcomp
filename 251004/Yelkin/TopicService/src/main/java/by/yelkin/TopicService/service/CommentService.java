package by.yelkin.TopicService.service;

import by.yelkin.TopicService.dto.comment.CommentRq;
import by.yelkin.TopicService.dto.comment.CommentRs;
import by.yelkin.TopicService.dto.comment.CommentUpdateRq;
import by.yelkin.TopicService.exception.ApiError;
import by.yelkin.TopicService.exception.ApiException;
import by.yelkin.TopicService.mapping.CommentMapper;
import by.yelkin.TopicService.repository.CommentRepository;
import by.yelkin.TopicService.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final CommentMapper commentMapper;

    public CommentRs create(CommentRq rq) {
        if (topicRepository.findById(rq.getTopicId()).isEmpty()) {
            throw new ApiException(ApiError.ERR_TOPIC_NOT_FOUND, rq.getTopicId().toString());
        }
        return commentMapper.toDto(commentRepository.save(commentMapper.fromDto(rq)));
    }

    public CommentRs readById(Long id) {
        return commentMapper.toDto(commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.ERR_COMMENT_NOT_FOUND, id.toString())));
    }

    public List<CommentRs> readAll() {
        return commentMapper.toDtoList(commentRepository.findAll());
    }

    public CommentRs update(CommentUpdateRq rq) {
        var comment = commentRepository.findById(rq.getId())
                .orElseThrow(() -> new ApiException(ApiError.ERR_COMMENT_NOT_FOUND, rq.getId().toString()));

        commentMapper.updateCreator(comment, rq);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    public void deleteById(Long id) {
        if (commentRepository.findById(id).isEmpty()) {
            throw new ApiException(ApiError.ERR_COMMENT_NOT_FOUND, id.toString());
        }
        commentRepository.deleteById(id);
    }
}
