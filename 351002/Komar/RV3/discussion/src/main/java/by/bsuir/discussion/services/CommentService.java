package by.bsuir.discussion.services;


import by.bsuir.discussion.dto.requests.CommentRequestDto;
import by.bsuir.discussion.dto.responses.CommentResponseDto;

import java.util.List;

public interface CommentService extends BaseService<CommentRequestDto, CommentResponseDto> {
    List<CommentResponseDto> readAll();
}
