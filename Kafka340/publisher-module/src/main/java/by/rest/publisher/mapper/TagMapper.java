package by.rest.publisher.mapper;

import by.rest.publisher.domain.Tag;
import by.rest.publisher.dto.TagRequestTo;
import by.rest.publisher.dto.TagResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stories", ignore = true)
    Tag toEntity(TagRequestTo dto);
    
    TagResponseTo toResponse(Tag entity);
}