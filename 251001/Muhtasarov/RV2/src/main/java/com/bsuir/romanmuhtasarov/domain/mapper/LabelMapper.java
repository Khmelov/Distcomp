package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.Label;
import com.bsuir.romanmuhtasarov.domain.request.LabelRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.LabelResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NewsListMapper.class)
public interface LabelMapper {
    Label toLabel(LabelRequestTo labelRequestTo);

    LabelResponseTo toLabelResponseTo(Label label);
}
