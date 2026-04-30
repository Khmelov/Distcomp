package org.polozkov.service.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentDiscussionRequest;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.entity.issue.Issue;
import org.polozkov.exception.InternalServerErrorException;
import org.polozkov.other.enums.RequestMethod;
import org.polozkov.other.record.CommentUploadRecord;
import org.polozkov.service.issue.IssueService;
import org.polozkov.service.kafka.KafkaService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CommentService {

    private final KafkaService kafkaService;

    public List<CommentResponseTo> getAllComments() {
        CommentUploadRecord record = new CommentUploadRecord(
                UUID.randomUUID(),
                RequestMethod.GET,
                null // Для getAll данные не нужны
        );
        return kafkaService.sendAndReceive(record);
    }

    public CommentResponseTo getComment(Long id) {
        // Создаем DTO для поиска, в котором заполнен только id
        CommentDiscussionRequest cdr = new CommentDiscussionRequest();
        cdr.setId(id);

        CommentUploadRecord record = new CommentUploadRecord(
                UUID.randomUUID(),
                RequestMethod.GET,
                cdr
        );

        List<CommentResponseTo> results = kafkaService.sendAndReceive(record);

        if (results == null || results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return results.get(0); // Берем первый и единственный элемент
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo commentRequest) {
        System.out.println("put zapros");
        CommentDiscussionRequest cdr = new CommentDiscussionRequest(commentRequest);

        if (cdr.getCountry() == null) {
            cdr.setCountry("BY");
        }

        CommentUploadRecord record = new CommentUploadRecord(
                UUID.randomUUID(),
                RequestMethod.PUT,
                cdr
        );

        List<CommentResponseTo> results = kafkaService.sendAndReceive(record);

        if (results == null || results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось обновить комментарий");
        }

        return results.get(0);
    }

    public CommentResponseTo createComment(CommentRequestTo request) {
        UUID requestId = UUID.randomUUID();
        CommentDiscussionRequest cdr = new CommentDiscussionRequest(request);
        cdr.setCountry("BY");

        CommentUploadRecord record = new CommentUploadRecord(
                requestId,
                RequestMethod.POST,
                cdr
        );

        List<CommentResponseTo> results = kafkaService.sendAndReceive(record);

        if (results == null || results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось создать комментарий");
        }

        return results.get(0);
    }

    public void deleteComment(Long id) {
        CommentDiscussionRequest cdr = new CommentDiscussionRequest();
        cdr.setId(id);

        CommentUploadRecord record = new CommentUploadRecord(
                UUID.randomUUID(),
                RequestMethod.DELETE,
                cdr
        );

        kafkaService.sendAndReceive(record);
    }
}