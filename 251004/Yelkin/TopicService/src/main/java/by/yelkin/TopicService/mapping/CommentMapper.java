package by.yelkin.TopicService.mapping;

import by.yelkin.TopicService.dto.comment.CommentRq;
import by.yelkin.TopicService.dto.comment.CommentRs;
import by.yelkin.TopicService.dto.comment.CommentUpdateRq;
import by.yelkin.TopicService.entity.Comment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "topic.id", source = "topicId")
    Comment fromDto(CommentRq rq);

    @Mapping(source = "topic.id", target = "topicId")
    CommentRs toDto(Comment comment);

    List<CommentRs> toDtoList(List<Comment> comments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCreator(@MappingTarget Comment comment, CommentUpdateRq rq);
}
