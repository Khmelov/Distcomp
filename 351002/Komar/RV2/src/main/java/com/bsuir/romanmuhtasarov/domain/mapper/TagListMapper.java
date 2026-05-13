package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.Tag;
import com.bsuir.romanmuhtasarov.domain.request.TagRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.TagResponseTo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = TagMapper.class)
public interface TagListMapper {
    List<Tag> toTagList(List<TagRequestTo> tagRequestToList);

    List<TagResponseTo> toTagResponseToList(List<Tag> tagList);
}
