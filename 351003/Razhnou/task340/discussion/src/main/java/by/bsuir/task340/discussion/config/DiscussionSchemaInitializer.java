package by.bsuir.task340.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DiscussionSchemaInitializer {
    private final CqlSession cqlSession;

    public DiscussionSchemaInitializer(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    @PostConstruct
    public void initializeSchema() {
        cqlSession.execute("""
                CREATE TABLE IF NOT EXISTS distcomp.tbl_reaction (
                    id bigint PRIMARY KEY,
                    tweet_id bigint,
                    content text,
                    state text
                )
                """);
        try {
            cqlSession.execute("ALTER TABLE distcomp.tbl_reaction ADD state text");
        } catch (Exception ignored) {
        }
    }
}
