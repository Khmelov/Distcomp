package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.Writer;
import by.bsuir.publisher.dto.responses.WriterResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = WriterResponseConverter.class)
public interface CollectionWriterResponseConverter {
    List<WriterResponseDto> toListDto(List<Writer> writers);
}