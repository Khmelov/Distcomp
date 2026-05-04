package com.messageservice.configs.cassandraconfig;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@EnableConfigurationProperties(DiscussionCassandraProperties.class)
public class CassandraConfig {

    @Bean(destroyMethod = "close")
    public CqlSession cqlSession(DiscussionCassandraProperties properties) {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(properties.getHost(), properties.getPort()))
                .withLocalDatacenter(properties.getLocalDatacenter())
                .build();
    }
}
