package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.CommentDto;
import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.model.Comment;
import by.bsuir.distcomp.model.Issue;
import by.bsuir.distcomp.repository.CommentRepository;
import by.bsuir.distcomp.repository.IssueRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService implements CrudService<CommentDto> {
    private final CommentRepository repository;
    private final IssueRepository issueRepository;

    public CommentService(CommentRepository repository, IssueRepository issueRepository) {
        this.repository = repository;
        this.issueRepository = issueRepository;
    }

    @Override
    public CommentDto create(CommentDto dto) {
        return toDto(repository.save(toEntity(new Comment(), dto)));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto get(Long id) {
        return toDto(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream().map(this::toDto).toList();
    }

    @Override
    public CommentDto update(Long id, CommentDto dto) {
        return toDto(repository.save(toEntity(findEntity(id), dto)));
    }

    @Override
    public void delete(Long id) {
        Comment comment = findEntity(id);
        repository.delete(comment);
    }

    private Comment findEntity(Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "40006", "Comment id is required");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "40403", "Comment not found"));
    }

    private Comment toEntity(Comment comment, CommentDto dto) {
        Issue issue = issueRepository.findById(dto.issueId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "40003", "Issue association not found"));
        comment.setIssue(issue);
        comment.setContent(dto.content());
        return comment;
    }

    private CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getIssue().getId(), comment.getContent());
    }
}
