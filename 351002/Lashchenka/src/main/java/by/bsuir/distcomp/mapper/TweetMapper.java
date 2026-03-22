package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.entity.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TweetMapper {
    Tweet toEntity(TweetRequestTo dto);
    TweetResponseTo toResponseDto(Tweet entity);
    void updateEntityFromDto(TweetRequestTo dto, @MappingTarget Tweet entity);
}
