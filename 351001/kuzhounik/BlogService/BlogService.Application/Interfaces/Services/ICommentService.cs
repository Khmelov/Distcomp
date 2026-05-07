using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Domain.Entities;

namespace BlogService.Application.Interfaces.Services;

public interface ICommentService : IService<long, CommentRequestToDto, CommentResponseToDto>
{
    
}