package com.rest.restapp.listener;

import com.common.NoteAsyncResponse;
import com.rest.restapp.service.NoteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResponseListener {

    NoteService service;

    @Bean
    public Consumer<NoteAsyncResponse> moderateNotice() {
        return service::handleResponse;
    }
}