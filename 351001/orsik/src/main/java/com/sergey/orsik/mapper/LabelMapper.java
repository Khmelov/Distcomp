package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.entity.Label;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    Label toEntity(LabelRequestTo request);

    LabelResponseTo toResponse(Label entity);
}
