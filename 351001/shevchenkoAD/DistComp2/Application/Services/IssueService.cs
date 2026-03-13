using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Services.Abstractions;
using Application.Services.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Interfaces;

namespace Application.Services;

public class IssueService : BaseService<Issue, IssueRequestTo, IssueResponseTo>, IIssueService {
    private readonly IRepository<Author> _authorRepository; 
    
    public IssueService(
        IRepository<Issue> repository, 
        IRepository<Author> authorRepository, 
        IMapper mapper) 
        : base(repository, mapper) 
    {
        _authorRepository = authorRepository;
    }

    public override async Task<IssueResponseTo> CreateAsync(IssueRequestTo request)
    {
        ValidateRequest(request);
        
        var authorExists = await _authorRepository.GetByIdAsync(request.AuthorId);
        if (authorExists == null)
        {
            throw new RestException(400, 27, $"Author with id {request.AuthorId} does not exist. Cannot create issue.");
        }
        
        bool exists = await _repository.ExistsAsync(i => i.Title == request.Title);
        if (exists)
        {
            throw new RestException(403, 26, $"Issue with title '{request.Title}' already exists");
        }
        
        return await base.CreateAsync(request);
        
    }
    
    protected override int NotFoundSubCode {
        get { return 25; }
    }

    protected override string EntityName {
        get { return "Issue"; }
    }

    protected override void ValidateRequest(IssueRequestTo req) {
        if (string.IsNullOrWhiteSpace(req.Title) || req.Title.Length < 2 || req.Title.Length > 64)
            throw new RestException(400, 21, "Title must be between 2 and 64 characters");

        if (string.IsNullOrWhiteSpace(req.Content) || req.Content.Length < 4 || req.Content.Length > 2048)
            throw new RestException(400, 22, "Content must be between 4 and 2048 characters");
    }

    protected override void BeforeCreate(Issue entity) {
        entity.Created = DateTime.UtcNow;
        entity.Modified = DateTime.UtcNow;
    }

    protected override void BeforeUpdate(Issue entity) {
        entity.Modified = DateTime.UtcNow;
    }
}