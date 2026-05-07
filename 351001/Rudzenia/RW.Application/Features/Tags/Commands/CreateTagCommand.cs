using AutoMapper;
using MediatR;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Tags.Commands;

public record CreateTagCommand(TagRequestTo Dto) : IRequest<TagResponseTo>;

public class CreateTagHandler : IRequestHandler<CreateTagCommand, TagResponseTo>
{
    private readonly IRepository<Tag> _repository;
    private readonly IMapper _mapper;

    public CreateTagHandler(IRepository<Tag> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<TagResponseTo> Handle(CreateTagCommand request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrEmpty(request.Dto.Name) || request.Dto.Name.Length < 2 || request.Dto.Name.Length > 32)
            throw new Exceptions.ValidationException("Name must be between 2 and 32 characters.");

        var entity = _mapper.Map<Tag>(request.Dto);
        var created = await _repository.CreateAsync(entity);
        return _mapper.Map<TagResponseTo>(created);
    }
}
