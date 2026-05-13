using System.Collections.Concurrent;
using System.Text;
using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Options;

namespace RW.Api.Kafka;

public class KafkaRequestClient : IKafkaRequestClient, IDisposable
{
    private readonly IProducer<string, string> _producer;
    private readonly KafkaSettings _settings;
    private readonly ILogger<KafkaRequestClient> _logger;
    private readonly ConcurrentDictionary<string, TaskCompletionSource<string>> _pending = new();

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public KafkaRequestClient(IOptions<KafkaSettings> settings, ILogger<KafkaRequestClient> logger)
    {
        _settings = settings.Value;
        _logger = logger;

        var config = new ProducerConfig
        {
            BootstrapServers = _settings.BootstrapServers,
            Acks = Acks.All,
            EnableIdempotence = true
        };
        _producer = new ProducerBuilder<string, string>(config).Build();
    }

    public async Task<KafkaResponseEnvelope> SendAndWaitAsync(
        string method,
        object? payload,
        string key,
        CancellationToken cancellationToken = default)
    {
        var correlationId = Guid.NewGuid().ToString();
        var tcs = new TaskCompletionSource<string>(TaskCreationOptions.RunContinuationsAsynchronously);
        _pending[correlationId] = tcs;

        var envelope = new KafkaRequestEnvelope { Method = method, Payload = payload };
        var json = JsonSerializer.Serialize(envelope, JsonOptions);

        var message = new Message<string, string>
        {
            Key = key,
            Value = json,
            Headers = new Headers
            {
                new Header("correlationId", Encoding.UTF8.GetBytes(correlationId)),
                new Header("replyTopic", Encoding.UTF8.GetBytes(_settings.OutTopic))
            }
        };

        try
        {
            await _producer.ProduceAsync(_settings.InTopic, message, cancellationToken);
            _logger.LogInformation("Produced {Method} request to {Topic} (correlationId={CorrelationId})",
                method, _settings.InTopic, correlationId);
        }
        catch
        {
            _pending.TryRemove(correlationId, out _);
            throw;
        }

        using var timeoutCts = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken);
        timeoutCts.CancelAfter(_settings.RequestTimeoutMs);

        await using var registration = timeoutCts.Token.Register(() =>
        {
            if (_pending.TryRemove(correlationId, out var pendingTcs))
                pendingTcs.TrySetException(new TimeoutException(
                    $"Kafka request {correlationId} timed out after {_settings.RequestTimeoutMs} ms"));
        });

        var responseJson = await tcs.Task;
        return JsonSerializer.Deserialize<KafkaResponseEnvelope>(responseJson, JsonOptions)
               ?? throw new InvalidOperationException("Empty Kafka response");
    }

    public void HandleResponse(string correlationId, string responseValue)
    {
        if (_pending.TryRemove(correlationId, out var tcs))
        {
            tcs.TrySetResult(responseValue);
        }
        else
        {
            _logger.LogWarning("Received Kafka response for unknown correlationId={CorrelationId}", correlationId);
        }
    }

    public void Dispose()
    {
        _producer.Flush(TimeSpan.FromSeconds(5));
        _producer.Dispose();
    }
}
