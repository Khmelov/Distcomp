using MediatR;
using Publisher.Application.Repositories;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.DeleteTopic;

public class DeleteTopicCommandHandler : IRequestHandler<DeleteTopicCommand, Result>
{
    private readonly ITopicRepository _topicRepository;
    private readonly IDatabase _redis;

    public DeleteTopicCommandHandler(ITopicRepository topicRepository, IConnectionMultiplexer conn)
    {
        _topicRepository = topicRepository;
        _redis = conn.GetDatabase();
    }

    public async Task<Result> Handle(DeleteTopicCommand request, CancellationToken cancellationToken)
    {
        var topicToDelete = await _topicRepository.GetByIdAsync(request.Id);

        if (topicToDelete == null)
        {
            return Result.Failure("Topic not found", ErrorType.NotFound);
        }

        var cachedTopic = await _redis.StringGetAsync($"topics:{request.Id}");
        if (cachedTopic.HasValue && !cachedTopic.IsNull)
        {
            await _redis.KeyDeleteAsync($"topics:{request.Id}");
        }

        await _topicRepository.DeleteAsync(topicToDelete);
        return Result.Success();
    }
}
