using MediatR;
using Publisher.Application.Repositories;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.DeleteLabel;

public class DeleteLabelCommandHandler : IRequestHandler<DeleteLabelCommand, Result>
{
    private readonly ILabelRepository _labelRepository;
    private readonly IDatabase _redis;
    
    public DeleteLabelCommandHandler(ILabelRepository labelRepository, IConnectionMultiplexer conn)
    {
        _labelRepository = labelRepository;
        _redis = conn.GetDatabase();
    }

    public async Task<Result> Handle(DeleteLabelCommand request, CancellationToken cancellationToken)
    {
        var labelToDelete = await _labelRepository.GetByIdAsync(request.Id);

        if (labelToDelete == null)
        {
            return Result.Failure("Label not found", ErrorType.NotFound);
        }
        
        var cachedLabel = await _redis.StringGetAsync($"labels:{request.Id}");
        if (cachedLabel.HasValue && !cachedLabel.IsNull)
        {
            await _redis.KeyDeleteAsync($"labels:{request.Id}");
        }

        await _labelRepository.DeleteAsync(labelToDelete);
        return Result.Success();
    }
}
