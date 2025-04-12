package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.request.CreatorRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CreatorResponseTo;
import org.mapstruct.Mapper;
import com.bsuir.romanmuhtasarov.domain.entity.Creator;

import java.util.List;

@Mapper(componentModel = "spring", uses = CreatorMapper.class)
public interface CreatorListMapper {
    List<Creator> toCreatorList(List<CreatorRequestTo> creatorRequestToList);

    List<CreatorResponseTo> toCreatorResponseToList(List<Creator> creatorList);
}
