package com.sergey.orsik.discussion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "publisher")
public record PublisherProperties(String baseUrl) {
}
