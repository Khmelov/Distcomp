using AutoMapper;
using MediatR;
using RW.Application.DTOs.Response;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Tags.Queries;

public record GetTagsQuery : IRequest<IEnumerable<TagResponseTo>>;

public class GetTagsHandler : IRequestHandler<GetTagsQuery, IEnumerable<TagResponseTo>>
{
    private readonly IRepository<Tag> _repository;
    private readonly IMapper _mapper;

    public GetTagsHandler(IRepository<Tag> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<IEnumerable<TagResponseTo>> Handle(GetTagsQuery request, CancellationToken cancellationToken)
    {
        var entities = await _repository.GetAllAsync();
        return _mapper.Map<IEnumerable<TagResponseTo>>(entities);
    }
}
