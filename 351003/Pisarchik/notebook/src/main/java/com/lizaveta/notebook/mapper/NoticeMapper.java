package com.lizaveta.notebook.mapper;

import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.entity.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoticeMapper {

    @Mapping(target = "id", ignore = true)
    Notice toEntity(NoticeRequestTo request);

    NoticeResponseTo toResponse(Notice entity);
}
