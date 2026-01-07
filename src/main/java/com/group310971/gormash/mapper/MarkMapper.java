package com.group310971.gormash.mapper;

import com.group310971.gormash.dto.MarkRequestTo;
import com.group310971.gormash.dto.MarkResponseTo;
import com.group310971.gormash.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MarkMapper {
    MarkMapper INSTANCE = Mappers.getMapper(MarkMapper.class);

    Mark toEntity(MarkRequestTo dto);

    MarkResponseTo toResponse(Mark entity);
}
