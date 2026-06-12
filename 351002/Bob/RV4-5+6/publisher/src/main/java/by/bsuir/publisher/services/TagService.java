package by.bsuir.publisher.services;

import by.bsuir.publisher.dto.requests.TagRequestDto;
import by.bsuir.publisher.dto.responses.TagResponseDto;

import java.util.List;

public interface TagService extends BaseService<TagRequestDto, TagResponseDto> {
    List<TagResponseDto> readAll();
}
