package com.tweetservice.configs.writerclientconfig;

import com.tweetservice.dtos.writer.WriterResponseTo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;

@HttpExchange("/api/v1.0")
public interface WriterClient {

    @GetExchange("/writers/{id}")
    public WriterResponseTo getWriterById(@PathVariable Long id);
}
