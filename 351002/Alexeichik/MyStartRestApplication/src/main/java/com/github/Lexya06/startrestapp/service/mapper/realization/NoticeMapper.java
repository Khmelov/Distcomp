package com.github.Lexya06.startrestapp.service.mapper.realization;
import com.github.Lexya06.startrestapp.model.dto.request.NoticeRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.NoticeResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface NoticeMapper extends GenericMapperImpl<Notice, NoticeRequestTo, NoticeResponseTo> {
    @Override
    @Mapping(source = "article.id", target = "articleId")
    NoticeResponseTo createResponseFromEntity(Notice entity);
}
