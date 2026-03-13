using AutoMapper;
using DistComp.Application.DTOs.Requests;
using DistComp.Application.DTOs.Responses;
using DistComp.Application.Exceptions;
using DistComp.Application.Services.Abstractions;
using DistComp.Application.Services.Interfaces;
using DistComp.Domain.Entities;
using DistComp.Domain.Interfaces;

namespace DistComp.Application.Services;

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