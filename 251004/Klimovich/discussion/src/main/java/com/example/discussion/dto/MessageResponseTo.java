package com.example.discussion.dto;

public class MessageResponseTo {
    private long id;

    private String content;

    private long issueId;

    public MessageResponseTo() {
    }

    public MessageResponseTo(long id, String content, long issueId){
        this.id = id;
        this.content = content;
        this.issueId = issueId;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIssueId() {
        return issueId;
    }

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
