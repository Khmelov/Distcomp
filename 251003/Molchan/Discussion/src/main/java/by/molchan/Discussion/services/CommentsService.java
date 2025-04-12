package by.molchan.Discussion.services;


import by.molchan.Discussion.DTOs.Requests.CommentRequestDTO;
import by.molchan.Discussion.DTOs.Responses.CommentResponseDTO;
import by.molchan.Discussion.models.Comment;
import by.molchan.Discussion.repositories.CommentsRepository;
import by.molchan.Discussion.utils.mappers.CommentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final CommentsMapper commentsMapper;
    @Value("${comment.country}")
    private String country;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository, CommentsMapper commentsMapper) {
        this.commentsRepository = commentsRepository;
        this.commentsMapper = commentsMapper;
    }

    public CommentResponseDTO save(CommentRequestDTO commentRequestDTO) {
        Comment comment = commentsMapper.toComment(commentRequestDTO);
        comment.getKey().setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
        return commentsMapper.toCommentResponse(commentsRepository.save(comment));
    }

    public List<CommentResponseDTO> findAll() {
        return commentsMapper.toCommentResponseList(commentsRepository.findAll());
    }

    public CommentResponseDTO findById(Long id) {
        return commentsMapper.toCommentResponse(
                commentsRepository.findByCountryAndId(country, id)
                        .orElseThrow(() -> new RuntimeException("Comment not found"))
        );
    }

    public void deleteById(long id) {
        commentsRepository.deleteByCountryAndId(country, id);
    }

    public CommentResponseDTO update(CommentRequestDTO commentRequestDTO) {
        Comment comment = commentsMapper.toComment(commentRequestDTO);
        comment.getKey().setId(commentRequestDTO.getId());
        return commentsMapper.toCommentResponse(commentsRepository.save(comment));
    }

}
