package com.blog.discussion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DiscussionApplicationTests {

    @Container
    static final CassandraContainer<?> cassandra =
            new CassandraContainer<>("cassandra:4.1")
                    .withExposedPorts(9042);

    @DynamicPropertySource
    static void cassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.contact-points",
                cassandra::getContainerIpAddress);
        registry.add("spring.data.cassandra.port",
                () -> cassandra.getMappedPort(9042));
        registry.add("spring.data.cassandra.local-datacenter",
                () -> "datacenter1");
    }

    @Test
    void contextLoads() {
        // Проверка загрузки контекста Spring
    }
}