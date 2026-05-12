package by.bsuir.task361.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscussionSchemaInitializer {
    private final CqlSession cqlSession;
    private final String keyspaceName;

    public DiscussionSchemaInitializer(
            CqlSession cqlSession,
            @Value("${spring.cassandra.keyspace-name}") String keyspaceName
    ) {
        this.cqlSession = cqlSession;
        this.keyspaceName = keyspaceName;
    }

    @PostConstruct
    public void initializeSchema() {
        String tableName = keyspaceName + ".tbl_reaction";
        cqlSession.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    id bigint PRIMARY KEY,
                    tweet_id bigint,
                    content text,
                    state text
                )
                """.formatted(tableName));
        try {
            cqlSession.execute("ALTER TABLE " + tableName + " ADD state text");
        } catch (Exception ignored) {
        }
    }
}
