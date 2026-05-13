package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.request.WriterRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.WriterResponseTo;
import org.mapstruct.Mapper;
import com.bsuir.romanmuhtasarov.domain.entity.Writer;

import java.util.List;

@Mapper(componentModel = "spring", uses = WriterMapper.class)
public interface WriterListMapper {
    List<Writer> toWriterList(List<WriterRequestTo> writerRequestToList);

    List<WriterResponseTo> toWriterResponseToList(List<Writer> writerList);
}
