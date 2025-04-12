package com.bsuir.romanmuhtasarov.serivces;

import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.request.MessageRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.MessageResponseTo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface MessageService {
    @Validated(ValidationMarker.OnCreate.class)
    MessageResponseTo create(@Valid MessageRequestTo entity);

    List<MessageResponseTo> read();

    @Validated(ValidationMarker.OnUpdate.class)
    MessageResponseTo update(@Valid MessageRequestTo entity);

    void delete(Long id);

    MessageResponseTo findMessageById(Long id);
}
