package by.molchan.Publisher.utils.mappers;

import by.molchan.Publisher.DTOs.Requests.LabelRequestDTO;
import by.molchan.Publisher.DTOs.Responses.LabelResponseDTO;
import by.molchan.Publisher.models.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabelsMapper {
    LabelResponseDTO toLabelResponse(Label label);
    List<LabelResponseDTO> toLabelResponseList(List<Label> labels);
    Label toLabel(LabelRequestDTO labelRequestDTO);
    List<Label> toLabelList(List<LabelRequestDTO> labelRequestDTOList);
}
