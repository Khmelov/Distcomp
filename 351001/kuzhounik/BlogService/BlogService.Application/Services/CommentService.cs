using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using BlogService.Domain.Interfaces;

namespace BlogService.Application.Services;

public class CommentService : BaseService<long, Comment, CommentRequestToDto, CommentResponseToDto>, ICommentService
{
    public CommentService(IRepository<long, Comment> repository,
        IRequestMapper<CommentRequestToDto, Comment> userRequestMapper,
        IResponseMapper<Comment, CommentResponseToDto> userResponseMapper) : base(repository, userRequestMapper, userResponseMapper){ }
}