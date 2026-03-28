package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.entity.Comment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @BeanMapping(unmappedSourcePolicy = ReportingPolicy.IGNORE)
    Comment toEntity(CommentRequestTo request);

    CommentResponseTo toResponse(Comment entity);
}
