package org.polozkov.service.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentDiscussionRequest;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.entity.issue.Issue;
import org.polozkov.exception.InternalServerErrorException;
import org.polozkov.service.issue.IssueService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final IssueService issueService;

    private final RestClient restClient = RestClient.create("http://localhost:24130/api/v1.0/comments");

    public List<CommentResponseTo> getAllComments() {
        return restClient.get()
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseTo>>() {});
    }

    public CommentResponseTo getComment(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found in discussion");
                })
                .body(CommentResponseTo.class);
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo commentRequest) {
        Issue issue = issueService.getIssueById(commentRequest.getIssueId());

        CommentDiscussionRequest cdr = new CommentDiscussionRequest(commentRequest);
        cdr.setCountry("BY");
        CommentResponseTo response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cdr)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InternalServerErrorException("Ошибка при сохранении в Cassandra");
                })
                .body(CommentResponseTo.class);

        return response;
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo commentRequest) {
        return restClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InternalServerErrorException("Ошибка при обновлении в Cassandra");
                })
                .body(CommentResponseTo.class);
    }

    public void deleteComment(Long id) {
        restClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InternalServerErrorException("Ошибка при удалении в Cassandra");
                })
                .toBodilessEntity();
    }
}