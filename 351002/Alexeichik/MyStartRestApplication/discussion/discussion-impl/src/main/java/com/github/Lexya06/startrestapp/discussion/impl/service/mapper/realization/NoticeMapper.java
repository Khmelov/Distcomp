package com.github.Lexya06.startrestapp.discussion.impl.service.mapper.realization;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeRequestTo;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeResponseTo;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.NoticeKey;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoticeMapper extends GenericMapperImpl<Notice, NoticeKey, NoticeRequestTo, NoticeResponseTo> {

    @Override
    @Mapping(target = "country", source = "id.country")
    @Mapping(target = "articleId", source = "id.articleId")
    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "state", source = "state")
    NoticeResponseTo createResponseFromEntity(Notice entity);

    @Override
    @Mapping(target = "id.country", source = "country")
    @Mapping(target = "id.articleId", source = "articleId")
    @Mapping(target = "content", source = "content")
    Notice createEntityFromRequest(NoticeRequestTo dto);

    @Override
    @Mapping(target = "id.country", source = "country")
    @Mapping(target = "id.articleId", source = "articleId")
    @Mapping(target = "content", source = "content")
    void updateEntityFromRequest(NoticeRequestTo dto, @MappingTarget Notice entity);
}
