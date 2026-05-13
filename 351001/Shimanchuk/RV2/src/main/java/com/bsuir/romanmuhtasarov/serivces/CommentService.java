package com.bsuir.romanmuhtasarov.serivces;

import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.request.CommentRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CommentResponseTo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface CommentService {
    @Validated(ValidationMarker.OnCreate.class)
    CommentResponseTo create(@Valid CommentRequestTo entity);

    List<CommentResponseTo> read();

    @Validated(ValidationMarker.OnUpdate.class)
    CommentResponseTo update(@Valid CommentRequestTo entity);

    void delete(Long id);

    CommentResponseTo findCommentById(Long id);
}
