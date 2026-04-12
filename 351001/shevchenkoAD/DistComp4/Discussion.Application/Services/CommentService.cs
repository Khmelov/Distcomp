using AutoMapper;
using Discussion.Application.Exceptions;
using Discussion.Application.Services.Abstractions;
using Discussion.Application.Services.Interfaces;
using Discussion.Domain.Entities;
using Discussion.Domain.Interfaces;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Discussion.Application.Services;

public class CommentService : BaseService<Comment, CommentRequestTo, CommentResponseTo>, ICommentService {
    private readonly ICommentRepository _commentRepository;

    public CommentService(ICommentRepository repository, IMapper mapper) 
        : base(repository, mapper) 
    {
        _commentRepository = repository;
    }
    
    public async Task<IEnumerable<CommentResponseTo>> GetByIssueIdAsync(long issueId)
    {
        var comments = await _commentRepository.GetByIssueIdAsync(issueId);
        return _mapper.Map<IEnumerable<CommentResponseTo>>(comments);
    }
    
    public async Task DeleteByIssueIdAsync(long issueId)
    {
        await _commentRepository.DeleteByIssueIdAsync(issueId);
    }
    
    public override async Task<CommentResponseTo> CreateAsync(CommentRequestTo request)
    {
        ValidateRequest(request);
        var entity = _mapper.Map<Comment>(request);
        
        if (entity.Id <= 0) 
        {
            entity.Id = DateTime.UtcNow.Ticks & long.MaxValue;
        }

        entity.Country = "Default";
        var created = await _repository.CreateAsync(entity);
        return _mapper.Map<CommentResponseTo>(created);
    }
    
    public override async Task<CommentResponseTo> UpdateAsync(CommentRequestTo request)
    {
        long id = request.Id ?? -1; 
        
        if (id < 0)
        {
            throw new RestException(400, NotFoundSubCode, "Invalid ID in request body");
        }
       
        var existing = await _commentRepository.GetByIdAsync(id);
        if (existing == null)
            throw new RestException(404, 45, "Comment not found");

        _mapper.Map(request, existing);
        var updated = await _commentRepository.UpdateAsync(existing);
        return _mapper.Map<CommentResponseTo>(updated);
    }

    protected override int NotFoundSubCode {
        get { return 45; }
    }

    protected override string EntityName {
        get { return "Comment"; }
    }

    protected override void ValidateRequest(CommentRequestTo req)
    {
        if (string.IsNullOrWhiteSpace(req.Content) || req.Content.Length < 2 || req.Content.Length > 2048)
            throw new RestException(400, 41, "Content length invalid");
    }
}