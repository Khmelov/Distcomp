package by.bsuir.publisher.dto.requests.converters;

import by.bsuir.publisher.domain.Writer;
import by.bsuir.publisher.dto.requests.WriterRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WriterRequestConverter {
    Writer fromDto(WriterRequestDto writer);
}
