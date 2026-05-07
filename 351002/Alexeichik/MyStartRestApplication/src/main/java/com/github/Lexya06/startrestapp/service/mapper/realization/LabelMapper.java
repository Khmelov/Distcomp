package com.github.Lexya06.startrestapp.service.mapper.realization;


import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.LabelResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface LabelMapper extends GenericMapperImpl<Label, LabelRequestTo, LabelResponseTo> {
}
