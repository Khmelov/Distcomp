package com.distcomp.publisher.kafka;

import com.distcomp.publisher.post.dto.PostRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostMessage {
    
    private final String operation;
    private final PostRequest postRequest;
    private final Long requestId;
    
    @JsonCreator
    public PostMessage(
            @JsonProperty("operation") String operation,
            @JsonProperty("postRequest") PostRequest postRequest,
            @JsonProperty("requestId") Long requestId) {
        this.operation = operation;
        this.postRequest = postRequest;
        this.requestId = requestId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public PostRequest getPostRequest() {
        return postRequest;
    }
    
    public Long getRequestId() {
        return requestId;
    }
}
