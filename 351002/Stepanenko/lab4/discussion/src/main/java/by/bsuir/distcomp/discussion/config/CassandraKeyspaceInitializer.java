package by.bsuir.distcomp.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.time.Duration;

public class CassandraKeyspaceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();

        // Читаем настройки из application.properties
        String host = env.getProperty("spring.cassandra.contact-points", "127.0.0.1").split(",")[0].trim();
        int port = env.getProperty("spring.cassandra.port", Integer.class, 9042);
        String dc = env.getProperty("spring.cassandra.local-datacenter", "datacenter1");

        // Настройка таймаутов
        DriverConfigLoader loader = DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
                .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(10))
                .build();

        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(dc) // КРИТИЧЕСКИ ВАЖНО: Добавлено явное указание DC
                .withConfigLoader(loader)
                .build()) {

            // Создание схемы
            session.execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");

            session.execute("CREATE TABLE IF NOT EXISTS distcomp.tbl_reaction (" +
                    "tweet_id bigint, id bigint, content text, state text, " +
                    "PRIMARY KEY ((tweet_id), id))");

            session.execute("CREATE TABLE IF NOT EXISTS distcomp.tbl_reaction_by_id (" +
                    "id bigint PRIMARY KEY, tweet_id bigint, content text, state text)");

            System.out.println("Cassandra schema initialized successfully.");
        } catch (Exception e) {
            // Логируем ошибку, но не даем приложению упасть, если Keyspace уже есть
            System.err.println("Note: Cassandra schema initialization skipped or failed: " + e.getMessage());
        }
    }
}