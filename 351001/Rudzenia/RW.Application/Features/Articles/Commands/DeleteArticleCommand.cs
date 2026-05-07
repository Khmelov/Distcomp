using MediatR;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Articles.Commands;

public record DeleteArticleCommand(long Id) : IRequest<Unit>;

public class DeleteArticleHandler : IRequestHandler<DeleteArticleCommand, Unit>
{
    private readonly IRepository<Article> _repository;

    public DeleteArticleHandler(IRepository<Article> repository)
    {
        _repository = repository;
    }

    public async Task<Unit> Handle(DeleteArticleCommand request, CancellationToken cancellationToken)
    {
        var deleted = await _repository.DeleteAsync(request.Id);
        if (!deleted)
            throw new NotFoundException("Article", request.Id);
        return Unit.Value;
    }
}
