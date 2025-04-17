package com.bsuir.discussion.dto.kafka;

import com.bsuir.discussion.dto.requests.PostRequestDTO;

public class InTopicDTO {
    private String method;
    private PostRequestDTO postRequestDTO;
    private String status;

    public InTopicDTO(String method, PostRequestDTO postRequestDTO, String status) {
        this.method = method;
        this.postRequestDTO = postRequestDTO;
        this.status = status;
    }

    public InTopicDTO() {}

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public PostRequestDTO getPostRequestDTO() { return postRequestDTO; }
    public void setPostRequestDTO(PostRequestDTO postRequestDTO) { this.postRequestDTO = postRequestDTO; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
