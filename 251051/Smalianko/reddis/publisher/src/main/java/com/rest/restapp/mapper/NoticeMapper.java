package com.rest.restapp.mapper;

import com.common.NoticeMessage;
import com.common.NoticeResponseToDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoticeMapper {

    NoticeResponseToDto mapToResponse(NoticeMessage message);
}