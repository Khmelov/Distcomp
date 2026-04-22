using AutoMapper;
using MediatR;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Tags.Commands;

public record UpdateTagCommand(long Id, TagRequestTo Dto) : IRequest<TagResponseTo>;

public class UpdateTagHandler : IRequestHandler<UpdateTagCommand, TagResponseTo>
{
    private readonly IRepository<Tag> _repository;
    private readonly IMapper _mapper;

    public UpdateTagHandler(IRepository<Tag> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<TagResponseTo> Handle(UpdateTagCommand request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrEmpty(request.Dto.Name) || request.Dto.Name.Length < 2 || request.Dto.Name.Length > 32)
            throw new ValidationException("Name must be between 2 and 32 characters.");

        var entity = _mapper.Map<Tag>(request.Dto);
        entity.Id = request.Id;
        var updated = await _repository.UpdateAsync(entity)
            ?? throw new NotFoundException("Tag", request.Id);
        return _mapper.Map<TagResponseTo>(updated);
    }
}
