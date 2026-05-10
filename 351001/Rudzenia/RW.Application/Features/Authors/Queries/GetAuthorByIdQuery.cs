using AutoMapper;
using MediatR;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Authors.Queries;

public record GetAuthorByIdQuery(long Id) : IRequest<AuthorResponseTo>;

public class GetAuthorByIdHandler : IRequestHandler<GetAuthorByIdQuery, AuthorResponseTo>
{
    private readonly IRepository<Author> _repository;
    private readonly IMapper _mapper;

    public GetAuthorByIdHandler(IRepository<Author> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<AuthorResponseTo> Handle(GetAuthorByIdQuery request, CancellationToken cancellationToken)
    {
        var entity = await _repository.GetByIdAsync(request.Id)
            ?? throw new NotFoundException("Author", request.Id);
        return _mapper.Map<AuthorResponseTo>(entity);
    }
}
