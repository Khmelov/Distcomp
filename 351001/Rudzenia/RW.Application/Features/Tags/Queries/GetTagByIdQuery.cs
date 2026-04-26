using AutoMapper;
using MediatR;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Tags.Queries;

public record GetTagByIdQuery(long Id) : IRequest<TagResponseTo>;

public class GetTagByIdHandler : IRequestHandler<GetTagByIdQuery, TagResponseTo>
{
    private readonly IRepository<Tag> _repository;
    private readonly IMapper _mapper;

    public GetTagByIdHandler(IRepository<Tag> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<TagResponseTo> Handle(GetTagByIdQuery request, CancellationToken cancellationToken)
    {
        var entity = await _repository.GetByIdAsync(request.Id)
            ?? throw new NotFoundException("Tag", request.Id);
        return _mapper.Map<TagResponseTo>(entity);
    }
}
