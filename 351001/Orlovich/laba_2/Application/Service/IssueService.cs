using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class IssueService
    : BaseService<Issue,IssueResponseTo,IssueRequestTo>
{
    private readonly IIssueRepository _issueRepository;
    private readonly IAuthorRepository _authorRepository;
    public IssueService(IIssueRepository issueRepository, IAuthorRepository authorRepository, IMapper mapper) : base(issueRepository, mapper)
    {
        _issueRepository = issueRepository;
        _authorRepository = authorRepository;
    }

    public override async Task<IssueResponseTo> AddAsync(IssueRequestTo item)
    {
        var a = _mapper.Map<Issue>(item);

        var b = await _issueRepository.GetByTitleAsync(a.title);

        if (b != null)
        {
            return null;
        }

        var c = await _authorRepository.GetByIdAsync(a.authorId);

        if (c == null)
        {
            return null;
        }

        await _repository.AddAsync(a);
        return _mapper.Map<IssueResponseTo>(a);
    }
}