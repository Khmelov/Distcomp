package by.rest.mapper;

import by.rest.domain.Story;
import by.rest.domain.Tag;
import by.rest.dto.StoryRequestTo;
import by.rest.dto.StoryResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Story toEntity(StoryRequestTo dto);
    
    @Mapping(source = "editor.id", target = "editorId")
    @Mapping(source = "tags", target = "tagIds", qualifiedByName = "tagsToIds")
    StoryResponseTo toResponse(Story entity);
    
    @Named("tagsToIds")
    default Set<Long> tagsToIds(Set<Tag> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
    }
}