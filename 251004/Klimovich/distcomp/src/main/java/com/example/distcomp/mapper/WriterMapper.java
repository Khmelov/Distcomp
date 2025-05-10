package com.example.distcomp.mapper;

import com.example.distcomp.dto.WriterRequestTo;
import com.example.distcomp.dto.WriterResponseTo;
import com.example.distcomp.model.Writer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    Writer toEntity(WriterRequestTo request);

    WriterResponseTo toResponse(Writer entity);

    List<Writer> listToEntity(List<WriterRequestTo> dtoList);

    List<WriterResponseTo> listToResponse(List<Writer> entityList);

}
