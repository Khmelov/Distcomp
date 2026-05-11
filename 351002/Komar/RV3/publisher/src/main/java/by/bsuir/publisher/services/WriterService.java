package by.bsuir.publisher.services;

import by.bsuir.publisher.dto.requests.WriterRequestDto;
import by.bsuir.publisher.dto.responses.WriterResponseDto;

import java.util.List;

public interface WriterService extends BaseService<WriterRequestDto, WriterResponseDto> {
    List<WriterResponseDto> readAll();
}
