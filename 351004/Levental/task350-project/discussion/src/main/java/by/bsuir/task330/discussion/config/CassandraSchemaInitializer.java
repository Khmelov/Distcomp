package by.bsuir.task330.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraSchemaInitializer {

    @Bean
    public ApplicationRunner cassandraInitRunner(CqlSession session) {
        return args -> {
            session.execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class':'SimpleStrategy','replication_factor':1}");
            session.execute("CREATE TABLE IF NOT EXISTS distcomp.tbl_notice (article_id bigint, id bigint, content text, PRIMARY KEY ((article_id), id))");
        };
    }
}
