package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.Label;
import com.bsuir.romanmuhtasarov.domain.request.LabelRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.LabelResponseTo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = LabelMapper.class)
public interface LabelListMapper {
    List<Label> toLabelList(List<LabelRequestTo> labelRequestToList);

    List<LabelResponseTo> toLabelResponseToList(List<Label> labelList);
}
