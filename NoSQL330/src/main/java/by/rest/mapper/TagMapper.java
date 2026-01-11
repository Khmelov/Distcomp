package by.rest.mapper;

import by.rest.domain.Tag;
import by.rest.dto.TagRequestTo;
import by.rest.dto.TagResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    
    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagRequestTo dto);
    
    TagResponseTo toResponse(Tag entity);
}