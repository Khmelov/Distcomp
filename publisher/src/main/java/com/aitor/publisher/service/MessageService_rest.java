package com.aitor.publisher.service;

import com.aitor.publisher.dto.MessageRequestTo;
import com.aitor.publisher.dto.MessageResponseTo;
import com.aitor.publisher.exception.EntityNotExistsException;
import com.aitor.publisher.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService_rest {
    private static final String BASE_URL = "http://localhost:24130/api/v1.0/messages";
    private final IssueRepository issueRepository;

    public MessageResponseTo add(MessageRequestTo requestBody){
        var message = new MessageRequestTo();
        message.setIssueId(getIssue(requestBody.getIssueId()));
        message.setContent(requestBody.getContent());
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = new RestTemplate().postForEntity(
                BASE_URL,
                new HttpEntity<>(message, headers),
                MessageResponseTo.class
        );
        return response.getBody();
    }

    public MessageResponseTo set(Long id, MessageRequestTo requestBody){
        var message = new MessageRequestTo(requestBody.getId(), getIssue(requestBody.getIssueId()), requestBody.getContent());
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var response = new RestTemplate().exchange(
                String.format("%s/%d", BASE_URL, id),
                HttpMethod.PUT,
                new HttpEntity<>(message, headers),
                MessageResponseTo.class
        );
        return response.getBody();
    }

    public MessageResponseTo get(Long id) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = new RestTemplate().getForEntity(
                String.format("%s/%d", BASE_URL, id),
                MessageResponseTo.class
        );
        return response.getBody();
    }

    public List<MessageResponseTo> getAll(){
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = new RestTemplate().getForEntity(
                BASE_URL,
                List.class
        );
        return response.getBody();
    }

    public MessageResponseTo remove(Long id) {
        try {
            var response = new RestTemplate().exchange(
                    String.format("%s/%d", BASE_URL, id),
                    HttpMethod.DELETE,
                    null,
                    MessageResponseTo.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new EntityNotExistsException();
        }
    }

    private Long getIssue(Long id){
        var entity = issueRepository.findById(id);
        if (entity.isPresent())
            return id;
        throw new EntityNotExistsException();
    }
}
