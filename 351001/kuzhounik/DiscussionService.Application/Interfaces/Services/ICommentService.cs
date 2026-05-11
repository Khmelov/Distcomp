using DiscussionService.Application.DTOs.Requests;
using DiscussionService.Application.DTOs.Responses;
using Shared.Application.Interfaces.Services;

namespace DiscussionService.Application.Interfaces.Services;

public interface ICommentService<Id> : IService<Id, CommentRequestToDto<Id>, CommentResponseToDto<Id>>
{
    
}