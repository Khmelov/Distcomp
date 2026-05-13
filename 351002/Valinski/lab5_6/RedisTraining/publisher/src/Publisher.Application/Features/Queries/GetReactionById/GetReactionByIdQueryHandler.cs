using System.Text.Json;
using MassTransit;
using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;
using Shared.Messages;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetReactionById;

public class GetReactionByIdQueryHandler : IRequestHandler<GetReactionByIdQuery, Result<ReactionResponseViewModel>>
{
    private readonly BrokerHelper _brokerHelper;
    private readonly ITopicProducer<ReactionMessage> _producer;
    private readonly IDatabase _redis;
    
    public GetReactionByIdQueryHandler(IConnectionMultiplexer connection, BrokerHelper brokerHelper, ITopicProducer<ReactionMessage> producer)
    {
        _brokerHelper = brokerHelper;
        _producer = producer;
        _redis = connection.GetDatabase();
    }

    public async Task<Result<ReactionResponseViewModel>> Handle(GetReactionByIdQuery request, CancellationToken cancellationToken)
    {
        string redisKey = $"reactions:{request.Id}";
        var cache = await _redis.StringGetAsync(redisKey);
        if (cache.HasValue)
        {
            return Result<ReactionResponseViewModel>.Success(JsonSerializer.Deserialize<ReactionResponseViewModel>(cache.ToString())!);
        }
        
        var correlationId = Guid.NewGuid();
        var msg = new ReactionMessage()
        {
            ActionType = ReactionMessages.GetById,
            CorrelationId = correlationId,
            Entity = JsonSerializer.SerializeToElement(request.Id),
        };
        
        await _producer.Produce(msg);
        var tcs = _brokerHelper.SetTask(correlationId.ToString());
        try
        { 
            var msgResult = await tcs.Task.WaitAsync(TimeSpan.FromMilliseconds(1000));
            if(msgResult is null)
                return Result<ReactionResponseViewModel>.Failure("No response from broker", ErrorType.Timeout);

            var reactionResult = JsonSerializer.Deserialize<ReactionResponseViewModel>(msgResult.Value);
            if (reactionResult is null)
            {
                return Result<ReactionResponseViewModel>.Failure("Reaction was not found", ErrorType.NotFound);
            }
            
            await _redis.StringSetAsync(redisKey, JsonSerializer.Serialize(reactionResult));
            return Result<ReactionResponseViewModel>.Success(reactionResult);
        }
        catch (TimeoutException e)
        {
            Console.WriteLine("Timeout exception:");
            throw;
        }
        
        
    }
}
