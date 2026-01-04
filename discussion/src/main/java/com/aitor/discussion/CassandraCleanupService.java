package com.aitor.discussion;

import com.aitor.discussion.model.Message;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CassandraCleanupService {

    private CassandraTemplate cassandraTemplate;

    @PostConstruct
    public void cleanTablesOnStartup() {
        cassandraTemplate.truncate(Message.class);
    }
}
