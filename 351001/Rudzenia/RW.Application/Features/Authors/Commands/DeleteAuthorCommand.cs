using MediatR;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Authors.Commands;

public record DeleteAuthorCommand(long Id) : IRequest<Unit>;

public class DeleteAuthorHandler : IRequestHandler<DeleteAuthorCommand, Unit>
{
    private readonly IRepository<Author> _repository;

    public DeleteAuthorHandler(IRepository<Author> repository)
    {
        _repository = repository;
    }

    public async Task<Unit> Handle(DeleteAuthorCommand request, CancellationToken cancellationToken)
    {
        var deleted = await _repository.DeleteAsync(request.Id);
        if (!deleted)
            throw new NotFoundException("Author", request.Id);
        return Unit.Value;
    }
}
