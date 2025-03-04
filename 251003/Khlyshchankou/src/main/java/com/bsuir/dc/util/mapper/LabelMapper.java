package com.bsuir.dc.util.mapper;

import com.bsuir.dc.dto.request.LabelRequestTo;
import com.bsuir.dc.dto.response.LabelResponseTo;
import com.bsuir.dc.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabelMapper {
    LabelResponseTo toLabelResponse(Label label);
    List<LabelResponseTo> toLabelResponseList(List<Label> labels);
    Label toLabel(LabelRequestTo labelRequestTo);
    List<Label> toLabelList(List<LabelRequestTo> labelRequestToList);
}