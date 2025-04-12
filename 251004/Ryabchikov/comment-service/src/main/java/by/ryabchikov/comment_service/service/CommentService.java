package by.ryabchikov.comment_service.service;

import by.ryabchikov.comment_service.dto.CommentRequestTo;
import by.ryabchikov.comment_service.dto.CommentResponseTo;
import by.ryabchikov.comment_service.dto.CommentUpdateRequestTo;
import java.util.List;

public interface CommentService {
    CommentResponseTo create(CommentRequestTo commentRequestTo);

    List<CommentResponseTo> readAll();

    CommentResponseTo readById(Long id);

    CommentResponseTo update(CommentUpdateRequestTo commentUpdateRequestTo);

    void deleteById(Long id);
}
