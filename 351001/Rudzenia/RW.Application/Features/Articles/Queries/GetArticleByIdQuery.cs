using AutoMapper;
using MediatR;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Articles.Queries;

public record GetArticleByIdQuery(long Id) : IRequest<ArticleResponseTo>;

public class GetArticleByIdHandler : IRequestHandler<GetArticleByIdQuery, ArticleResponseTo>
{
    private readonly IRepository<Article> _repository;
    private readonly IMapper _mapper;

    public GetArticleByIdHandler(IRepository<Article> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<ArticleResponseTo> Handle(GetArticleByIdQuery request, CancellationToken cancellationToken)
    {
        var entity = await _repository.GetByIdAsync(request.Id)
            ?? throw new NotFoundException("Article", request.Id);
        return _mapper.Map<ArticleResponseTo>(entity);
    }
}
