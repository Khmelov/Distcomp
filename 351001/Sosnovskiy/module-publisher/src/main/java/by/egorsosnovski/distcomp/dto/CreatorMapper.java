package by.egorsosnovski.distcomp.dto;

import by.egorsosnovski.distcomp.entities.Creator;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreatorMapper {

    CreatorResponseTo out(Creator creator);

    Creator in(CreatorRequestTo creator);
}
