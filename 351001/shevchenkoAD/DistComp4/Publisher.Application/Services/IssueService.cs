using AutoMapper;
using Publisher.Domain.Entities;
using Publisher.Domain.Interfaces;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Abstractions;
using Publisher.Application.Services.Interfaces;

namespace Publisher.Application.Services;

public class IssueService : BaseService<Issue, IssueRequestTo, IssueResponseTo>, IIssueService 
{
    private readonly IIssueRepository _issueRepository; 
    private readonly IRepository<Author> _authorRepository; 
    private readonly IRepository<Label> _labelRepository; 
    private readonly ICommentService _commentService; 

    public IssueService(
        IIssueRepository repository, 
        IRepository<Author> authorRepository, 
        IRepository<Label> labelRepository, 
        IMapper mapper,
        ICommentService commentService) 
        : base(repository, mapper) 
    {
        _issueRepository = repository;
        _authorRepository = authorRepository;
        _labelRepository = labelRepository;
        _commentService = commentService;
    }

     
    public override async Task<IssueResponseTo> GetByIdAsync(long id)
    {
         
        var issueDto = await base.GetByIdAsync(id);
        
        var comments = await _commentService.GetByIssueIdAsync(id);
        
        issueDto.Comments = comments.ToList(); 

        return issueDto;
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
        
        var issue = _mapper.Map<Issue>(request);
        BeforeCreate(issue);
        
        if (request.Labels != null && request.Labels.Any())
        {
            foreach (var labelName in request.Labels)
            {
                var existingLabel = (await _labelRepository.GetAllAsync())
                    .FirstOrDefault(l => l.Name == labelName);

                if (existingLabel != null)
                {
                    issue.Labels.Add(existingLabel);
                }
                else
                {
                    var newLabel = new Label { Name = labelName };
                    issue.Labels.Add(newLabel);
                }
            }
        }

        var createdIssue = await _repository.CreateAsync(issue);
        return _mapper.Map<IssueResponseTo>(createdIssue);
    }
    
    public override async Task<bool> DeleteAsync(long id)
    {
         
        var issue = await _issueRepository.GetByIdWithLabelsAsync(id);
        if (issue == null)
            ThrowNotFound(id);
        
        var labelsToCheck = issue.Labels.ToList();
        
        await _issueRepository.DeleteAsync(id);
        
        await _commentService.DeleteByIssueIdAsync(id);
         
        foreach (var label in labelsToCheck)
        {
            bool stillInUse = await _issueRepository.IsLabelUsedAsync(label.Id);
            if (!stillInUse)
            {
                await _labelRepository.DeleteAsync(label.Id);
            }
        }

        return true;
    }
     
    
    protected override int NotFoundSubCode => 25;
    protected override string EntityName => "Issue";

    protected override void ValidateRequest(IssueRequestTo req) {
        if (string.IsNullOrWhiteSpace(req.Title) || req.Title.Length < 2 || req.Title.Length > 64)
            throw new RestException(400, 21, "Title must be between 2 and 64 characters");

        if (string.IsNullOrWhiteSpace(req.Content) || req.Content.Length < 4 || req.Content.Length > 2048)
            throw new RestException(400, 22, "Content must be between 4 and 2048 characters");
        
        if (req.Labels != null)
        {
            foreach (var label in req.Labels)
            {
                if (label.Length < 2 || label.Length > 32)
                {
                    throw new RestException(400, 31, $"Label '{label}' length must be 2..32 chars");
                }
            }
        }
    }

    protected override void BeforeCreate(Issue entity) {
        entity.Created = DateTime.UtcNow;
        entity.Modified = DateTime.UtcNow;
    }

    protected override void BeforeUpdate(Issue entity) {
        entity.Modified = DateTime.UtcNow;
    }
}