using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class AuthorService 
    : BaseService<Author,AuthorResponseTo,AuthorRequestTo>
{
    private readonly IAuthorRepository _repository;
    public AuthorService(IAuthorRepository repository, IMapper mapper) : base(repository, mapper)
    {
        _repository = repository;
    }

    public override async Task<AuthorResponseTo> AddAsync(AuthorRequestTo item)
    {
        var a = _mapper.Map<Author>(item);

        var b = await _repository.GetByLoginAsync(a.login);

        if (b != null)
        {
            return null;
        }

        await _repository.AddAsync(a);
        return _mapper.Map<AuthorResponseTo>(a);
    }
}