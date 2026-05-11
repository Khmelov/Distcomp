using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using Microsoft.Extensions.Caching.Distributed;
using Shared.Application.Interfaces.Mappers;
using Shared.Application.Services;
using Shared.Domain.Interfaces;

namespace BlogService.Application.Services;

public class CommentService<Id> : BaseService<Id, Comment<Id>, CommentRequestToDto<Id>, CommentResponseToDto<Id>>, ICommentService<Id>
{
    public CommentService(IRepository<Id, Comment<Id>> repository,
        IRequestMapper<CommentRequestToDto<Id>, Comment<Id>> userRequestMapper,
        IResponseMapper<Comment<Id>, CommentResponseToDto<Id>> userResponseMapper, 
        IDistributedCache cache) : 
        base(repository, userRequestMapper, userResponseMapper, cache){ }
    
    protected override string GetCacheKey(Id id) => $"Blog:Comment:{id}";
}