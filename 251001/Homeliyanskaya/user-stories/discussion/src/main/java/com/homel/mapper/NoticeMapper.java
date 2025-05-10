package com.homel.mapper;

import com.homel.dto.NoticeRequestTo;
import com.homel.dto.NoticeResponseTo;
import com.homel.model.Notice;
import jakarta.validation.constraints.Null;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface NoticeMapper {
    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    public static UUID convertLongToUUID(Long id) {
        return new UUID(id, id);
    }

    public static Long convertUUIDToLong(UUID id) {
        return id.getMostSignificantBits();
    }

    default Notice toEntity(NoticeRequestTo dto) {
        Notice notice = new Notice();
        notice.setId(convertLongToUUID((dto.getId() == null) ? 0 : dto.getId()));
        notice.setStoryId(convertLongToUUID((dto.getStoryId() == null) ? 0 : dto.getStoryId()));
        notice.setContent(dto.getContent());
        return notice;
    }

    default NoticeResponseTo toResponse(Notice notice) {
        NoticeResponseTo response = new NoticeResponseTo();
        response.setId(convertUUIDToLong(notice.getId()));
        response.setContent(notice.getContent());
        response.setStoryId(convertUUIDToLong(notice.getStoryId()));
        return response;
    }
}
