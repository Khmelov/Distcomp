package com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization;


import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface LabelMapper extends GenericMapperImpl<Label, LabelRequestTo, LabelResponseTo> {
}
