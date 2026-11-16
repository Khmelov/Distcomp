package by.bsuir.discussion;

import by.bsuir.discussion.config.CassandraKeyspaceInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        LiquibaseAutoConfiguration.class
})
public class DiscussionApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DiscussionApplication.class);
        application.addInitializers(new CassandraKeyspaceInitializer());
        application.run(args);
    }
}
