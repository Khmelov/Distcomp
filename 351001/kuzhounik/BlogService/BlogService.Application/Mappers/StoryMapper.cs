using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Domain.Entities;
using Shared.Application.Interfaces.Mappers;

namespace BlogService.Application.Mappers;

public class StoryMapper<Id> : IRequestMapper<StoryRequestToDto<Id>, Story<Id>>, IResponseMapper<Story<Id>, StoryResponseToDto<Id>>
{
    public Story<Id> Map(StoryRequestToDto<Id> dto)
    {
        return new Story<Id>()
        {
            ID = dto.ID,
            UserID = dto.UserID,
            Title = dto.Title,
            Content = dto.Content,
            Modified = DateTime.UtcNow,
        };
    }

    public StoryResponseToDto<Id> Map(Story<Id> entity)
    {
        return new StoryResponseToDto<Id>()
        {
            ID = entity.ID,
            UserID = entity.UserID,
            Title = entity.Title,
            Content = entity.Content,
        };
    }
}