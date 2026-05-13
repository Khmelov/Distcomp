using System.Text.Json;
using AutoMapper;
using MassTransit;
using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;
using Shared.Messages;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.UpdateReaction;

public class UpdateReactionCommandHandler : IRequestHandler<UpdateReactionCommand, Result<ReactionResponseViewModel>>
{
    private readonly ITopicProducer<ReactionMessage> _producer;
    private readonly BrokerHelper _brokerHelper;
    private readonly IMapper _mapper;
    private readonly IDatabase _redis;

    public UpdateReactionCommandHandler(IConnectionMultiplexer connection, ITopicProducer<ReactionMessage> producer, BrokerHelper brokerHelper, IMapper mapper)
    {
        _producer = producer;
        _brokerHelper = brokerHelper;
        _mapper = mapper;
        _redis = connection.GetDatabase();
    }

    public async Task<Result<ReactionResponseViewModel>> Handle(UpdateReactionCommand request, CancellationToken cancellationToken)
    {
        var correlationId = Guid.NewGuid();
        var updateMsg = new UpdateReactionRequestMsg()
        {
            Id = request.Id,
            Content = request.Content,
            Country = request.Country,
            TopicId = request.TopicId,
        };
        
        var reactionMsg = new ReactionMessage()
        {
            CorrelationId = correlationId,
            ActionType = ReactionMessages.Update,
            Entity = JsonSerializer.SerializeToElement(updateMsg)
        };
        var tcs = _brokerHelper.SetTask(correlationId.ToString());
        await _producer.Produce(reactionMsg);
        try
        {
            var msgResult = await tcs.Task.WaitAsync(TimeSpan.FromSeconds(1000));
            if(msgResult is null)
                return Result<ReactionResponseViewModel>.Failure("No response from broker", ErrorType.Timeout);
            
            var result = msgResult.Value.Deserialize<ReactionResponseMsg>();
            if (result.Status == ReactionStatuses.Declined)
            {
                return Result<ReactionResponseViewModel>.Failure("Declined", ErrorType.NotFound);
            }
            
            var reactionViewModel = _mapper.Map<ReactionResponseViewModel>(result);
            
            string redisKey = $"reactions:{request.Id}";
            await _redis.StringSetAsync(redisKey, JsonSerializer.SerializeToUtf8Bytes(reactionViewModel));
            
            return Result<ReactionResponseViewModel>.Success(reactionViewModel);
        }
        catch (TimeoutException e)
        {
            Console.WriteLine("-------------------timeout---------------------");
            throw;
        }
    }
}
