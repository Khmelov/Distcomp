using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Domain.Entities;

namespace BlogService.Application.Mappers;

public class CommentMapper : IRequestMapper<CommentRequestToDto, Comment>, IResponseMapper<Comment, CommentResponseToDto>
{
    public Comment Map(CommentRequestToDto dto)
    {
        return new Comment()
        {
            ID = dto.ID,
            StoryID = dto.StoryID,
            Content = dto.Content,
        };
    }

    public CommentResponseToDto Map(Comment entity)
    {
        return new CommentResponseToDto()
        {
            ID =  entity.ID,
            StoryID =  entity.StoryID,
            Content = entity.Content
        };
    }
}