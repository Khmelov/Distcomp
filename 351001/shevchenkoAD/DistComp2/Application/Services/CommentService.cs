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
    private readonly IRepository<Issue> _issueRepository; 
    
    public CommentService(
        IRepository<Comment> repository,
        IRepository<Issue> issueRepository, 
        IMapper mapper) 
        : base(repository, mapper) 
    {
        _issueRepository = issueRepository;
    }

    public override async Task<CommentResponseTo> CreateAsync(CommentRequestTo request)
    {
        ValidateRequest(request);
        
        var issueExists = await _issueRepository.GetByIdAsync(request.IssueId);
        if (issueExists == null)
        {
            throw new RestException(400, 27, $"Issue with id {request.IssueId} does not exist. Cannot create comment.");
        }
        
        return await base.CreateAsync(request);
        
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