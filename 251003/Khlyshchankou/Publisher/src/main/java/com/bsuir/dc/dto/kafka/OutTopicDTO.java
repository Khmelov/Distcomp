package com.bsuir.dc.dto.kafka;

import com.bsuir.dc.dto.response.PostResponseTo;

import java.util.List;

public class OutTopicDTO {
    private PostResponseTo postResponseDTO;
    private List<PostResponseTo> postResponsesListDTO;
    private String status;
    private String error;

    public PostResponseTo getPostResponseDTO() { return postResponseDTO; }
    public void setPostResponseDTO(PostResponseTo postResponseDTO) { this.postResponseDTO = postResponseDTO;}

    public List<PostResponseTo> getPostResponsesListDTO() {return postResponsesListDTO; }
    public void setPostResponsesListDTO(List<PostResponseTo> postResponsesListDTO) {
        this.postResponsesListDTO = postResponsesListDTO;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public OutTopicDTO() {}
}