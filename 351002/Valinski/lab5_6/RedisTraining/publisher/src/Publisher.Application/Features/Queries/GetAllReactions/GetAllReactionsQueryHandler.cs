using System.Text.Json;
using MassTransit;
using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;
using Shared.Messages;

namespace Publisher.Application.Features.Queries.GetAllReactions;

public class
    GetAllReactionsQueryHandler : IRequestHandler<GetAllReactionsQuery, Result<List<ReactionResponseViewModel>>>
{
    private readonly ITopicProducer<ReactionMessage> _producer;
    private readonly BrokerHelper _brokerHelper;
        
    public GetAllReactionsQueryHandler(ITopicProducer<ReactionMessage> producer, BrokerHelper brokerHelper)
    {
        _producer = producer;
        _brokerHelper = brokerHelper;
    }

    public async Task<Result<List<ReactionResponseViewModel>>> Handle(GetAllReactionsQuery request,
        CancellationToken cancellationToken)
    {
        var correlationId = Guid.NewGuid();
        var msg = new ReactionMessage()
        {
            ActionType = ReactionMessages.GetAll,
            CorrelationId = correlationId,
        };
        
        await _producer.Produce(msg);
        var tcs = _brokerHelper.SetTask(correlationId.ToString());
        var msgResult = await tcs.Task.WaitAsync(TimeSpan.FromMilliseconds(1000));
        if(msgResult is null)
            return Result<List<ReactionResponseViewModel>>.Failure("No response from broker", ErrorType.Timeout);
        
        var list = JsonSerializer.Deserialize<List<ReactionResponseViewModel>>(msgResult.Value);
        return Result<List<ReactionResponseViewModel>>.Success(list!);
    }
}
