package com.distcomp.publisher.service;

import com.distcomp.publisher.dto.NoteRequestDTO;
import com.distcomp.publisher.dto.NoteResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class NoteService {

    @Autowired
    private WebClient discussionWebClient;

    public List<NoteResponseDTO> getAllNotes() {
        return discussionWebClient.get()
                .uri("/notes")
                .retrieve()
                .bodyToFlux(NoteResponseDTO.class)
                .collectList()
                .block();
    }

    public List<NoteResponseDTO> getNotesByTweetId(Long tweetId) {
        return discussionWebClient.get()
                .uri("/notes/tweet/{tweetId}", tweetId)
                .retrieve()
                .bodyToFlux(NoteResponseDTO.class)
                .collectList()
                .block();
    }

    public NoteResponseDTO getNote(Long tweetId, Long id) {
        return discussionWebClient.get()
                .uri("/notes/{tweetId}/{id}", tweetId, id)
                .retrieve()
                .bodyToMono(NoteResponseDTO.class)
                .block();
    }

    public NoteResponseDTO getNoteById(Long id) {
        return discussionWebClient.get()
                .uri("/notes/{id}", id)
                .retrieve()
                .bodyToMono(NoteResponseDTO.class)
                .block();
    }

    public NoteResponseDTO createNote(NoteRequestDTO request) {
        return discussionWebClient.post()
                .uri("/notes")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseDTO.class)
                .block();
    }

    public NoteResponseDTO updateNote(Long tweetId, Long id, NoteRequestDTO request) {
        return discussionWebClient.put()
                .uri("/notes/{tweetId}/{id}", tweetId, id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseDTO.class)
                .block();
    }

    public NoteResponseDTO updateNoteById(Long id, NoteRequestDTO request) {
        return discussionWebClient.put()
                .uri("/notes/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseDTO.class)
                .block();
    }

    public void deleteNote(Long tweetId, Long id) {
        discussionWebClient.delete()
                .uri("/notes/{tweetId}/{id}", tweetId, id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void deleteNoteById(Long id) {
        discussionWebClient.delete()
                .uri("/notes/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}