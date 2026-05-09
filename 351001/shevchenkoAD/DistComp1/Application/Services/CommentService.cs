using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Services.Abstractions;
using Application.Services.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Interfaces;

namespace Application.Services;

public class CommentService : BaseService<Comment, CommentRequestTo, CommentResponseTo>, ICommentService {
    public CommentService(IRepository<Comment> repository,
                          IMapper mapper)
        : base(repository, mapper) {
    }

    protected override int NotFoundSubCode {
        get { return 45; }
    }

    protected override string EntityName {
        get { return "Comment"; }
    }

    protected override void ValidateRequest(CommentRequestTo req) {
        if (string.IsNullOrWhiteSpace(req.Content) || req.Content.Length < 2 || req.Content.Length > 2048)
            throw new RestException(400, 41, "Content must be between 2 and 2048 characters");
    }
}