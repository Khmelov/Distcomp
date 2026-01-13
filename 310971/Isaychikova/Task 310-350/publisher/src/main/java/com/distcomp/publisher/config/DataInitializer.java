package com.distcomp.publisher.config;

import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.repo.WriterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WriterRepository writerRepository;

    public DataInitializer(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    public void run(String... args) {
        if (writerRepository.count() > 0) {
            return;
        }

        Writer writer = new Writer();
        writer.setLogin("evgeniabelovagomel@gmail.com");
        writer.setPassword("topic123");
        writer.setFirstname("Евгения");
        writer.setLastname("Исайчикова");
        writerRepository.save(writer);
    }
}
