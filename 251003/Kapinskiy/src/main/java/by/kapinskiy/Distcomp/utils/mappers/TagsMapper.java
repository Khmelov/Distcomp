package by.kapinskiy.Distcomp.utils.mappers;

import by.kapinskiy.Distcomp.DTOs.Requests.TagRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.TagResponseDTO;
import by.kapinskiy.Distcomp.models.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagsMapper {
    TagResponseDTO toTagResponse(Tag tag);
    List<TagResponseDTO> toTagResponseList(List<Tag> tags);
    Tag toTag(TagRequestDTO tagRequestDTO);
    List<Tag> toTagList(List<TagRequestDTO> tagRequestDTOList);
}
