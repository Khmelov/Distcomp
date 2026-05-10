package com.adashkevich.kafka.lab.discussion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicProperties {
    private String inTopic;
    private String outTopic;

    public String getInTopic() { return inTopic; }
    public void setInTopic(String inTopic) { this.inTopic = inTopic; }
    public String getOutTopic() { return outTopic; }
    public void setOutTopic(String outTopic) { this.outTopic = outTopic; }
}
