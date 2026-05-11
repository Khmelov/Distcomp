using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using Shared.Application.Interfaces.Services;

namespace BlogService.Application.Interfaces.Services;

public interface ICommentService<Id> : IService<Id, CommentRequestToDto<Id>, CommentResponseToDto<Id>>
{
    
}