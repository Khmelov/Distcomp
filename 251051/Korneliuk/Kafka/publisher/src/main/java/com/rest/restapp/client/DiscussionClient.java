package com.rest.restapp.client;

import com.common.NoteResponseTo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class DiscussionClient {

    private final WebClient webClient;

    public DiscussionClient() {
        this.webClient = WebClient
                .builder()
                .baseUrl("http://localhost:24130/api/v1.0")
                .build();
    }

    public List<NoteResponseTo> getNotesByIssueId(Long issueId) {
        return webClient
                .get()
                .uri("/notices/issue/" + issueId)
                .retrieve()
                .bodyToFlux(NoteResponseTo.class)
                .collectList()
                .block();
    }
}
