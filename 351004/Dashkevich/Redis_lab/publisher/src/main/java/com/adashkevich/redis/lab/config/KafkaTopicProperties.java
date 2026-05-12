package com.adashkevich.redis.lab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicProperties {
    private String inTopic;
    private String outTopic;
    private long replyTimeoutMs = 1000;

    public String getInTopic() { return inTopic; }
    public void setInTopic(String inTopic) { this.inTopic = inTopic; }
    public String getOutTopic() { return outTopic; }
    public void setOutTopic(String outTopic) { this.outTopic = outTopic; }
    public long getReplyTimeoutMs() { return replyTimeoutMs; }
    public void setReplyTimeoutMs(long replyTimeoutMs) { this.replyTimeoutMs = replyTimeoutMs; }
}
