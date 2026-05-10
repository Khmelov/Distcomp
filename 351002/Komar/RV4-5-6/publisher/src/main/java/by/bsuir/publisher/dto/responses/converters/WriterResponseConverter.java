package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.Writer;
import by.bsuir.publisher.dto.responses.WriterResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WriterResponseConverter {
    WriterResponseDto toDto(Writer writer);
}
