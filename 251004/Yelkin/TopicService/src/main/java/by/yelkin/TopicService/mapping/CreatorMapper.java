package by.yelkin.TopicService.mapping;

import by.yelkin.TopicService.dto.creator.CreatorRq;
import by.yelkin.TopicService.dto.creator.CreatorRs;
import by.yelkin.TopicService.dto.creator.CreatorUpdateRq;
import by.yelkin.TopicService.entity.Creator;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreatorMapper {
    Creator fromDto(CreatorRq rq);

    CreatorRs toDto(Creator creator);

    List<CreatorRs> toDtoList(List<Creator> creators);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCreator(@MappingTarget Creator creator, CreatorUpdateRq rq);
}
