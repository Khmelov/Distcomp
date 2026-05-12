using System.Collections.Concurrent;
using System.Text;
using System.Text.Json;
using Confluent.Kafka;
using IdGen;
using Microsoft.Extensions.Options;
using Publisher.Presentation.Contracts;
using Publisher.Presentation.Options;

namespace Publisher.Presentation.Clients;

public class ReactionsServiceClient : IDisposable
{
    private readonly KafkaReactionConnectionOptions _kafkaReactionConnectionOptions;
    private readonly IProducer<string, string> _producer;
    private readonly ConcurrentDictionary<string, TaskCompletionSource<string>> _pendingRequests = new();
    private readonly IdGenerator _idGenerator = new(0);
    private const long Seconds = 1000;

    public ReactionsServiceClient(IOptions<KafkaReactionConnectionOptions> kafkaConnectionOptions)
    {
        _kafkaReactionConnectionOptions = kafkaConnectionOptions.Value;

        var config = new ProducerConfig()
        {
            BootstrapServers = _kafkaReactionConnectionOptions.BootstrapServers,
        };

        _producer = new ProducerBuilder<string, string>(config).Build();
    }


    public async Task<List<ReactionResponse>?> GetAllReactions()
    {
        var res = await SendMessage("GetAll", "GetAll");
        return JsonSerializer.Deserialize<List<ReactionResponse>>(res);
    }

    public void SendResult(string correlationId, string result)
    {
        if (_pendingRequests.TryRemove(correlationId, out var tcs))
        {
            tcs.SetResult(result);
        }
    }

    private async Task<string> SendMessage(string action, string value)
    {
        string correlationId = Guid.NewGuid().ToString();
        var tcs = new TaskCompletionSource<string>(TaskCreationOptions.RunContinuationsAsynchronously);
        
        _pendingRequests[correlationId] = tcs;

        var message = new Message<string, string>()
        {
            Key = Guid.NewGuid().ToString(),
            Headers = new Headers()
            {
                { "CorrelationId", Encoding.UTF8.GetBytes(correlationId) },
                { "Action", Encoding.UTF8.GetBytes(action) },
                { "ReplyTo", Encoding.UTF8.GetBytes(_kafkaReactionConnectionOptions.OutTopic) }
            },
            Value = value,
        };

        await _producer.ProduceAsync(_kafkaReactionConnectionOptions.InTopic, message);

        try
        {
            return await tcs.Task.WaitAsync(TimeSpan.FromSeconds(Seconds));
        }
        catch (TimeoutException)
        {
            _pendingRequests.TryRemove(correlationId, out _);
            throw;
        }
    }

    public async Task<long> CreateReaction(CreateReactionRequest request)
    {
        long id = _idGenerator.CreateId();
        var brokerRequest = new CreateReactionBrokerRequest(request)
        {
            Id = id,
        };

        var val = JsonSerializer.Serialize(brokerRequest);
        var msg = new Message<string, string>()
        {
            Headers = new Headers() { { "Action", Encoding.UTF8.GetBytes("Create") } },
            Value = val,
        };

        await _producer.ProduceAsync(_kafkaReactionConnectionOptions.InTopic, msg);
        return id;
    }

    public async Task<ReactionResponse?> GetReactionById(long id)
    {
        var res = await SendMessage("GetById", id.ToString());
        return JsonSerializer.Deserialize<ReactionResponse>(res);
    }

    public async Task<ReactionResponse?> UpdateReaction(ReactionUpdateRequest request)
    {
        var res = await SendMessage("Update", JsonSerializer.Serialize(request));
        return JsonSerializer.Deserialize<ReactionResponse>(res);
    }
    
    public async Task<bool> DeleteReaction(long id)
    {
        var res = await SendMessage("DeleteById", id.ToString());
        return res == "Success";
    }

    public void Dispose()
    {
        _producer.Dispose();
    }
}
