package by.bsuir.distcomp.discussion;

import by.bsuir.distcomp.discussion.config.CassandraKeyspaceInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscussionApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DiscussionApplication.class);
        app.addInitializers(new CassandraKeyspaceInitializer());
        app.run(args);
    }
}
