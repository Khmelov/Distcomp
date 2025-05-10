package com.example.distcomp.mapper;

import com.example.distcomp.dto.LabelRequestTo;
import com.example.distcomp.dto.LabelResponseTo;
import com.example.distcomp.model.Label;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    Label toEntity(LabelRequestTo request);
    LabelResponseTo toResponse(Label entity);

    List<Label> listToEntity(List<LabelRequestTo> dtoList);

    List<LabelResponseTo> listToResponse(List<Label> entityList);

}
