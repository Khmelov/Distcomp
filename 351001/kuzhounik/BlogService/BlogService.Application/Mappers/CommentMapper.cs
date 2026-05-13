using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Domain.Entities;
using Shared.Application.Interfaces.Mappers;

namespace BlogService.Application.Mappers;

public class CommentMapper<Id> : IRequestMapper<CommentRequestToDto<Id>, Comment<Id>>, IResponseMapper<Comment<Id>, CommentResponseToDto<Id>>
{
    public Comment<Id> Map(CommentRequestToDto<Id> dto)
    {
        return new Comment<Id>()
        {
            ID = dto.ID,
            StoryID = dto.StoryID,
            Content = dto.Content,
        };
    }

    public CommentResponseToDto<Id> Map(Comment<Id> entity)
    {
        return new CommentResponseToDto<Id>()
        {
            ID =  entity.ID,
            StoryID =  entity.StoryID,
            Content = entity.Content
        };
    }
}