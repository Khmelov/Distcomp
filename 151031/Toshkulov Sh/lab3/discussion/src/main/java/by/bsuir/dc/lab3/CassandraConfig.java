package by.bsuir.dc.lab3;

import com.datastax.oss.driver.api.core.CqlSession;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {
    public @Bean CqlSession session(){
        return CqlSession.builder().withKeyspace("distcomp").build();
    }
}