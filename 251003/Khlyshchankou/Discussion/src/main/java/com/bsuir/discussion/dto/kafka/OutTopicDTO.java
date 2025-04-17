package com.bsuir.discussion.dto.kafka;

import com.bsuir.discussion.dto.responses.PostResponseDTO;

import java.util.List;

public class OutTopicDTO {
    private PostResponseDTO postResponseDTO;
    private List<PostResponseDTO> postResponsesListDTO;
    private String status;
    private String error;

    public OutTopicDTO(PostResponseDTO postResponseDTO, String status) {
        this.postResponseDTO = postResponseDTO;
        this.status = status;
    }

    public OutTopicDTO(List<PostResponseDTO> postResponsesListDTO, String status) {
        this.status = status;
        this.postResponsesListDTO = postResponsesListDTO;
    }

    public OutTopicDTO( String error, String status) {
        this.status = status;
        this.error = error;
    }

    public OutTopicDTO() {}

    public PostResponseDTO getPostResponseDTO() { return postResponseDTO; }
    public void setPostResponseDTO(PostResponseDTO postResponseDTO) {this.postResponseDTO = postResponseDTO; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public List<PostResponseDTO> getPostResponsesListDTO() { return postResponsesListDTO; }
    public void setPostResponsesListDTO(List<PostResponseDTO> postResponsesListDTO) {
        this.postResponsesListDTO = postResponsesListDTO;
    }
}
