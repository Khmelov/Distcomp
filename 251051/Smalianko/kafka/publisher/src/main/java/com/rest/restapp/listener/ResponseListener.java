package com.rest.restapp.listener;

import com.common.NoticeAsyncResponse;
import com.rest.restapp.service.NoticeService;
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

    NoticeService service;

    @Bean
    public Consumer<NoticeAsyncResponse> moderateNotice() {
        return service::handleResponse;
    }
}