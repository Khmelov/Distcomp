package com.github.Lexya06.startrestapp.discussion.impl.service.mapper.realization;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.NoticeKey;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl.GenericKeyMapperImpl;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoticeKeyMapper extends GenericKeyMapperImpl<NoticeKey, NoticeKeyDto> {

}
