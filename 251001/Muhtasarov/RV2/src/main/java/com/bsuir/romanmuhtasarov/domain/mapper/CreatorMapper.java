package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.response.CreatorResponseTo;
import org.mapstruct.Mapper;
import com.bsuir.romanmuhtasarov.domain.entity.Creator;
import com.bsuir.romanmuhtasarov.domain.request.CreatorRequestTo;

@Mapper(componentModel = "spring", uses = NewsListMapper.class)
public interface CreatorMapper {
    Creator toCreator(CreatorRequestTo creatorRequestTo);

    CreatorResponseTo toCreatorResponseTo(Creator creator);
}
