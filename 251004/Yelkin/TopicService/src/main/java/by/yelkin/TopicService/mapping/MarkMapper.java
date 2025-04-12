package by.yelkin.TopicService.mapping;

import by.yelkin.TopicService.dto.mark.MarkRq;
import by.yelkin.TopicService.dto.mark.MarkRs;
import by.yelkin.TopicService.dto.mark.MarkUpdateRq;
import by.yelkin.TopicService.entity.Mark;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    Mark fromDto(MarkRq rq);

    MarkRs toDto(Mark mark);

    List<MarkRs> toDtoList(List<Mark> marks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMark(@MappingTarget Mark mark, MarkUpdateRq rq);
}
