package by.bsuir.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import java.net.InetSocketAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {
    private final String host;
    private final int port;
    private final String datacenter;
    private final String keyspace;

    public CassandraConfig(
            @Value("${discussion.cassandra.host}") String host,
            @Value("${discussion.cassandra.port}") int port,
            @Value("${discussion.cassandra.datacenter}") String datacenter,
            @Value("${discussion.cassandra.keyspace}") String keyspace) {
        this.host = host;
        this.port = port;
        this.datacenter = datacenter;
        this.keyspace = keyspace;
    }

    @Bean(destroyMethod = "close")
    public CqlSession cqlSession() {
        CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(datacenter)
                .build();
        session.execute(String.format("""
                CREATE KEYSPACE IF NOT EXISTS %s
                WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                """, keyspace));
        session.execute(String.format("""
                CREATE TABLE IF NOT EXISTS %s.tbl_comment (
                    id bigint PRIMARY KEY,
                    issue_id bigint,
                    content text,
                    state text
                )
                """, keyspace));
        try {
            session.execute(String.format("ALTER TABLE %s.tbl_comment ADD state text", keyspace));
        } catch (InvalidQueryException ignored) {
            // Existing installations from Task330 may already have the column.
        }
        return session;
    }
}
