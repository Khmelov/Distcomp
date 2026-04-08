package com.messageservice.configs.cassandraconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "discussion.cassandra")
public class DiscussionCassandraProperties {

    private String host = "localhost";

    private int port = 9042;

    private String keyspace = "distcomp";

    private String localDatacenter = "datacenter1";

    private int bucketCount = 8;
}
