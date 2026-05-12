package com.adashkevich.kafka.lab.kafka;

public class KafkaRequestMessage {
    private String correlationId;
    private KafkaOperation operation;
    private Long id;
    private Long newsId;
    private String content;

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public KafkaOperation getOperation() { return operation; }
    public void setOperation(KafkaOperation operation) { this.operation = operation; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
