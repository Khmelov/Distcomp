package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.entity.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TweetMapper {
    @Mapping(target = "marks", ignore = true)
    Tweet toEntity(TweetRequestTo dto);

    TweetResponseTo toResponseDto(Tweet entity);

    @Mapping(target = "marks", ignore = true)
    void updateEntityFromDto(TweetRequestTo dto, @MappingTarget Tweet entity);
}
