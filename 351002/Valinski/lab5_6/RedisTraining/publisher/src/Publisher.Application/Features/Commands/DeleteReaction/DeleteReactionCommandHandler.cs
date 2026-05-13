using System.Text.Json;
using MassTransit;
using MediatR;
using Shared.Commons;
using Shared.Messages;

namespace Publisher.Application.Features.Commands.DeleteReaction;

public class DeleteReactionCommandHandler : IRequestHandler<DeleteReactionCommand, Result>
{
    private readonly ITopicProducer<ReactionMessage> _producer;
    private readonly BrokerHelper _brokerHelper;

    public DeleteReactionCommandHandler(ITopicProducer<ReactionMessage> producer, BrokerHelper brokerHelper)
    {
        _producer = producer;
        _brokerHelper = brokerHelper;
    }

    public async Task<Result> Handle(DeleteReactionCommand request, CancellationToken cancellationToken)
    {
        var correlationId = Guid.NewGuid();

        var reactionMsg = new ReactionMessage()
        {
            ActionType = ReactionMessages.Delete,
            CorrelationId = correlationId,
            Entity = JsonSerializer.SerializeToElement(request.Id),
        };
        
        var tcs = _brokerHelper.SetTask(correlationId.ToString());
        await _producer.Produce(reactionMsg);
        try
        {
            var msgResult = await tcs.Task.WaitAsync(TimeSpan.FromSeconds(1));
            if (msgResult is null)
            {
                return Result.Failure("No response from broker", ErrorType.Timeout);
            }
            return Result.Success();
        }
        catch(TimeoutException e)
        {
            Console.WriteLine(e);
            throw;
        }
    }
}
