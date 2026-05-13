package by.bsuir.romamuhtasarov.api;

import by.bsuir.romamuhtasarov.impl.bean.Writer;
import by.bsuir.romamuhtasarov.impl.dto.WriterRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.WriterResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WriterMapper {
    WriterMapper INSTANCE = Mappers.getMapper(WriterMapper.class);

    WriterResponseTo WriterToWriterResponseTo(Writer writer);

    WriterRequestTo WriterToWriterRequestTo(Writer writer);

    Writer WriterResponseToToWriter(WriterResponseTo writerResponseTo);

    Writer WriterRequestToToWriter(WriterRequestTo writerRequestTo);
}