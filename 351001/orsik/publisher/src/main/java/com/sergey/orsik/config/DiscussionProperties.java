package com.sergey.orsik.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discussion")
public record DiscussionProperties(String baseUrl) {
}
