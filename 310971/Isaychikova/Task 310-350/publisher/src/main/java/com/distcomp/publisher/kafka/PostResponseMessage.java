package com.distcomp.publisher.kafka;

import com.distcomp.publisher.post.dto.PostResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PostResponseMessage {
    
    private final Long requestId;
    private final PostResponse postResponse;
    private final List<PostResponse> postResponses;
    private final String error;
    private final boolean deleted;
    
    @JsonCreator
    public PostResponseMessage(
            @JsonProperty("requestId") Long requestId,
            @JsonProperty("postResponse") PostResponse postResponse,
            @JsonProperty("postResponses") List<PostResponse> postResponses,
            @JsonProperty("error") String error,
            @JsonProperty("deleted") boolean deleted) {
        this.requestId = requestId;
        this.postResponse = postResponse;
        this.postResponses = postResponses;
        this.error = error;
        this.deleted = deleted;
    }
    
    public static PostResponseMessage single(PostResponse postResponse, Long requestId) {
        return new PostResponseMessage(requestId, postResponse, null, null, false);
    }
    
    public static PostResponseMessage list(List<PostResponse> postResponses, Long requestId) {
        return new PostResponseMessage(requestId, null, postResponses, null, false);
    }
    
    public static PostResponseMessage error(String error, Long requestId) {
        return new PostResponseMessage(requestId, null, null, error, false);
    }
    
    public static PostResponseMessage deleted(boolean deleted, Long requestId) {
        return new PostResponseMessage(requestId, null, null, null, deleted);
    }
    
    public Long getRequestId() {
        return requestId;
    }
    
    public PostResponse getPostResponse() {
        return postResponse;
    }
    
    public List<PostResponse> getPostResponses() {
        return postResponses;
    }
    
    public String getError() {
        return error;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
