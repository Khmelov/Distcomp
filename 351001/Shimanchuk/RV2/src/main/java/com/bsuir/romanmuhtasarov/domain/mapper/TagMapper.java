package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.Tag;
import com.bsuir.romanmuhtasarov.domain.request.TagRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.TagResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NewsListMapper.class)
public interface TagMapper {
    Tag toTag(TagRequestTo tagRequestTo);

    TagResponseTo toTagResponseTo(Tag tag);
}
