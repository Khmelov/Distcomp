package com.example.rest.mapper;
import com.example.rest.dto.requestDto.LabelRequestTo;
import com.example.rest.dto.responseDto.LabelResponseTo;
import com.example.rest.dto.updateDto.LabelUpdateTo;
import com.example.rest.entity.Label;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelResponseTo ToLabelResponseTo(Label Label);
    Label ToLabel(LabelRequestTo LabelRequestTo);
    Label ToLabel(LabelUpdateTo LabelUpdateTo);
}
