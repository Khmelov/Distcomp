using AutoMapper;
using MediatR;
using RW.Application.DTOs.Response;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Articles.Queries;

public record GetArticlesQuery : IRequest<IEnumerable<ArticleResponseTo>>;

public class GetArticlesHandler : IRequestHandler<GetArticlesQuery, IEnumerable<ArticleResponseTo>>
{
    private readonly IRepository<Article> _repository;
    private readonly IMapper _mapper;

    public GetArticlesHandler(IRepository<Article> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<IEnumerable<ArticleResponseTo>> Handle(GetArticlesQuery request, CancellationToken cancellationToken)
    {
        var entities = await _repository.GetAllAsync();
        return _mapper.Map<IEnumerable<ArticleResponseTo>>(entities);
    }
}
