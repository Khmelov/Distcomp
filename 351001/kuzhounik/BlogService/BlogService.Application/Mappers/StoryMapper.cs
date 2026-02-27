using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Domain.Entities;

namespace BlogService.Application.Mappers;

public class StoryMapper : IRequestMapper<StoryRequestToDto, Story>, IResponseMapper<Story, StoryResponseToDto>
{
    public Story Map(StoryRequestToDto dto)
    {
        return new Story()
        {
            ID = dto.ID,
            UserID = dto.UserID,
            Title = dto.Title,
            Content = dto.Content,
            Modified =  DateTime.Now,
        };
    }

    public StoryResponseToDto Map(Story entity)
    {
        return new StoryResponseToDto()
        {
            ID = entity.ID,
            UserID = entity.UserID,
            Title = entity.Title,
            Content = entity.Content,
        };
    }
}