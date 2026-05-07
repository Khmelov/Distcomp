using MediatR;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Tags.Commands;

public record DeleteTagCommand(long Id) : IRequest<Unit>;

public class DeleteTagHandler : IRequestHandler<DeleteTagCommand, Unit>
{
    private readonly IRepository<Tag> _repository;

    public DeleteTagHandler(IRepository<Tag> repository)
    {
        _repository = repository;
    }

    public async Task<Unit> Handle(DeleteTagCommand request, CancellationToken cancellationToken)
    {
        var deleted = await _repository.DeleteAsync(request.Id);
        if (!deleted)
            throw new NotFoundException("Tag", request.Id);
        return Unit.Value;
    }
}
