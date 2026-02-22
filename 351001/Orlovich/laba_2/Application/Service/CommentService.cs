using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class CommentService
    : BaseService<Comment,CommentResponseTo,CommentRequestTo>
{
    private readonly IIssueRepository _issueRepository;
    private readonly IRepository<Comment> _commentRepository;
    public CommentService(IIssueRepository issueRepository, IRepository<Comment> commentRepository, IMapper mapper) : base(commentRepository, mapper)
    {
        _issueRepository = issueRepository;
        _commentRepository = commentRepository;
    }

    public override async Task<CommentResponseTo> AddAsync(CommentRequestTo item)
    {
        var a = _mapper.Map<Comment>(item);

        var b = await _issueRepository.GetByIdAsync(a.issueId);

        if (b == null)
        {
            return null;
        }

        await _repository.AddAsync(a);
        return _mapper.Map<CommentResponseTo>(a);
    }
}