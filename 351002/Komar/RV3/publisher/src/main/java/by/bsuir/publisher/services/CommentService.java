package by.bsuir.publisher.services;

import by.bsuir.publisher.dto.requests.CommentRequestDto;
import by.bsuir.publisher.dto.responses.CommentResponseDto;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import jakarta.validation.Valid;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    CommentResponseDto create(@Valid @NonNull CommentRequestDto dto) throws EntityExistsException;
    Optional<CommentResponseDto> read(@NonNull Long uuid);
    List<CommentResponseDto> readAll();
    CommentResponseDto update(@Valid @NonNull CommentRequestDto dto) throws NoEntityExistsException;
    Long delete(@NonNull Long uuid) throws NoEntityExistsException;
}
