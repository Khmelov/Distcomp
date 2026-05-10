using AutoMapper;
using MediatR;
using RW.Application.DTOs.Response;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Authors.Queries;

public record GetAuthorsQuery : IRequest<IEnumerable<AuthorResponseTo>>;

public class GetAuthorsHandler : IRequestHandler<GetAuthorsQuery, IEnumerable<AuthorResponseTo>>
{
    private readonly IRepository<Author> _repository;
    private readonly IMapper _mapper;

    public GetAuthorsHandler(IRepository<Author> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<IEnumerable<AuthorResponseTo>> Handle(GetAuthorsQuery request, CancellationToken cancellationToken)
    {
        var entities = await _repository.GetAllAsync();
        return _mapper.Map<IEnumerable<AuthorResponseTo>>(entities);
    }
}
