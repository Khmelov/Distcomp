package com.example.news.mapper;

import com.example.common.dto.WriterRequestTo;
import com.example.common.dto.WriterResponseTo;
import com.example.news.entity.Writer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface WriterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "articles", ignore = true)
    Writer toEntity(WriterRequestTo request);

    WriterResponseTo toResponse(Writer entity);
}