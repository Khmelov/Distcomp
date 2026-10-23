package com.distcomp.mapper;

import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.model.Notice;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

/**
 * Converts {@link Notice} entities to and from their transport objects.
 */
@Mapper(componentModel = "spring")
public interface NoticeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issue", ignore = true)
    Notice toEntity(NoticeRequestTo request);

    @Mapping(target = "issueId", source = "issue.id")
    NoticeResponseTo toResponse(Notice notice);

    List<NoticeResponseTo> toResponseList(Collection<Notice> notices);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issue", ignore = true)
    void updateEntity(NoticeRequestTo request, @MappingTarget Notice notice);
}
