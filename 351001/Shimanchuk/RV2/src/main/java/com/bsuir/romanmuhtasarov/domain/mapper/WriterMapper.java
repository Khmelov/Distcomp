package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.response.WriterResponseTo;
import org.mapstruct.Mapper;
import com.bsuir.romanmuhtasarov.domain.entity.Writer;
import com.bsuir.romanmuhtasarov.domain.request.WriterRequestTo;

@Mapper(componentModel = "spring", uses = NewsListMapper.class)
public interface WriterMapper {
    Writer toWriter(WriterRequestTo writerRequestTo);

    WriterResponseTo toWriterResponseTo(Writer writer);
}
