package by.bsuir.discussion.services.impl;

import by.bsuir.discussion.domain.Comment;
import by.bsuir.discussion.dto.requests.CommentRequestDto;
import by.bsuir.discussion.dto.requests.converters.CommentRequestConverter;
import by.bsuir.discussion.dto.responses.CommentResponseDto;
import by.bsuir.discussion.dto.responses.converters.CollectionCommentResponseConverter;
import by.bsuir.discussion.dto.responses.converters.CommentResponseConverter;
import by.bsuir.discussion.exceptions.EntityExistsException;
import by.bsuir.discussion.exceptions.Comments;
import by.bsuir.discussion.exceptions.NoEntityExistsException;
import by.bsuir.discussion.repositories.CommentRepository;
import by.bsuir.discussion.services.CommentService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentRequestConverter commentRequestConverter;
    private final CommentResponseConverter commentResponseConverter;
    private final CollectionCommentResponseConverter collectionCommentResponseConverter;
    @Override
    @Validated
    public CommentResponseDto create(@Valid @NonNull CommentRequestDto dto) throws EntityExistsException {
        Optional<Comment> comment = dto.getId() == null ? Optional.empty() : commentRepository.findCommentById(dto.getId());
        if (comment.isEmpty()) {
            Comment entity = commentRequestConverter.fromDto(dto);
            entity.setId((long) (Math.random() * 2_000_000_000L) + 1);
            return commentResponseConverter.toDto(commentRepository.save(entity));
        } else {
            throw new EntityExistsException(Comments.EntityExistsException);
        }
    }

    @Override
    public Optional<CommentResponseDto> read(@NonNull Long id) {
        return commentRepository.findCommentById(id).flatMap(writer -> Optional.of(
                commentResponseConverter.toDto(writer)));
    }

    @Override
    @Validated
    public CommentResponseDto update(@Valid @NonNull CommentRequestDto dto) throws NoEntityExistsException {
        Optional<Comment> comment = dto.getId() == null || commentRepository.findCommentByNewsIdAndId(
                dto.getNewsId(), dto.getId()).isEmpty() ?
                Optional.empty() : Optional.of(commentRequestConverter.fromDto(dto));
        return commentResponseConverter.toDto(commentRepository.save(comment.orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException))));
    }

    @Override
    public Long delete(@NonNull Long id) throws NoEntityExistsException {
        Optional<Comment> comment = commentRepository.findCommentById(id);
        commentRepository.deleteCommentByNewsIdAndId(comment.map(Comment::getNewsId).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)), comment.map(Comment::getId).
                orElseThrow(() -> new NoEntityExistsException(Comments.NoEntityExistsException)));
        return comment.get().getId();
    }

    @Override
    public List<CommentResponseDto> readAll() {
        return collectionCommentResponseConverter.toListDto(commentRepository.findAll());
    }
}