using DiscussionService.Application.DTOs.Requests;
using DiscussionService.Application.DTOs.Responses;
using DiscussionService.Application.Interfaces.Services;
using DiscussionService.Domain.Entities;
using Microsoft.Extensions.Caching.Distributed;
using Shared.Application.Interfaces.Mappers;
using Shared.Application.Services;
using Shared.Domain.Interfaces;

namespace DiscussionService.Application.Services;

public class CommentService<Id> : BaseService<Id, Comment<Id>, CommentRequestToDto<Id>, CommentResponseToDto<Id>>, ICommentService<Id>
{
    public CommentService(IRepository<Id, Comment<Id>> repository,
        IRequestMapper<CommentRequestToDto<Id>, Comment<Id>> userRequestMapper,
        IResponseMapper<Comment<Id>, CommentResponseToDto<Id>> userResponseMapper, 
        IDistributedCache cache) : 
        base(repository, userRequestMapper, userResponseMapper, cache){ }
    
    protected override string GetCacheKey(Id id) => $"Disc:Comment:{id}";
}