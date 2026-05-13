using System.Text.Json;
using MassTransit;
using Microsoft.Extensions.Logging;
using Shared.Commons;
using Shared.Messages;

namespace Publisher.Infrastructure.Broker.Consumers;

public class ReactionConsumer : IConsumer<ReactionMessage>
{
    private readonly ILogger<ReactionConsumer> _logger;
    private readonly BrokerHelper _brokerHelper;

    public ReactionConsumer(ILogger<ReactionConsumer> logger, BrokerHelper brokerHelper)
    {
        _logger = logger;
        _brokerHelper = brokerHelper;
    }

    public async Task Consume(ConsumeContext<ReactionMessage> context)
    {
        var reactionResponse = context.Message;
        var correlationId = reactionResponse.CorrelationId;
        if (correlationId == null)
        {
            _logger.LogWarning("Received a message without CorrelationId. Ignoring.");
            return;
        }

        _brokerHelper.TryRemove(correlationId.Value.ToString(), reactionResponse.Entity);
    }
}
