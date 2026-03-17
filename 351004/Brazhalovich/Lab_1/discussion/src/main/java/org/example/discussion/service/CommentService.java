package org.example.discussion.service;

import lombok.RequiredArgsConstructor;
import org.example.discussion.dto.request.CommentRequestTo;
import org.example.discussion.dto.response.CommentResponseTo;
import org.example.discussion.entity.Comment;
import org.example.discussion.exception.NotFoundException;
import org.example.discussion.mapper.CommentMapper;
import org.example.discussion.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.example.discussion.service.IdGenerator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final IdGenerator idGenerator = new IdGenerator();

    public CommentResponseTo create(CommentRequestTo request) {
        Comment comment = commentMapper.toEntity(request);
        comment.setId(idGenerator.nextId());
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public List<CommentResponseTo> findAll() {
        return commentRepository.findAll()
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentResponseTo findById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        return commentMapper.toDto(comment);
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        commentMapper.updateEntityFromDto(request, comment);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}