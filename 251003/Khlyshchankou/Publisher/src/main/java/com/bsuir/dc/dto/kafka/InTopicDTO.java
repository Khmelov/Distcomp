package com.bsuir.dc.dto.kafka;

import com.bsuir.dc.dto.request.PostRequestTo;

public class InTopicDTO {
    private String method;
    private PostRequestTo postRequestDTO;
    private String status;

    public InTopicDTO(String method, PostRequestTo postRequestDTO, String status) {
        this.method = method;
        this.postRequestDTO = postRequestDTO;
        this.status = status;
    }

    public InTopicDTO() {}

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public PostRequestTo getPostRequestDTO() { return postRequestDTO; }
    public void setPostRequestDTO(PostRequestTo postRequestDTO) { this.postRequestDTO = postRequestDTO; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}