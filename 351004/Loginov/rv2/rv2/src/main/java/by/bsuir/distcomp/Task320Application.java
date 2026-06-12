package by.bsuir.distcomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "by.bsuir.distcomp",
        exclude = {
                CassandraAutoConfiguration.class,
                CassandraDataAutoConfiguration.class,
                CassandraRepositoriesAutoConfiguration.class,
                CassandraReactiveDataAutoConfiguration.class,
                CassandraReactiveRepositoriesAutoConfiguration.class
        })
public class Task320Application {
    public static void main(String[] args) {
        SpringApplication.run(Task320Application.class, args);
    }
}
