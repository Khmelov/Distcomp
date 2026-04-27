package com.example.demo.mapper;

import com.example.demo.dto.request.WriterRequestTo;
import com.example.demo.dto.response.WriterResponseTo;
import com.example.demo.models.Writer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WriterMapper {
    Writer requestToEntity(WriterRequestTo request);
    WriterResponseTo toResponse(Writer writer);
    List<WriterResponseTo> toResponseList(List<Writer> writers);
}
