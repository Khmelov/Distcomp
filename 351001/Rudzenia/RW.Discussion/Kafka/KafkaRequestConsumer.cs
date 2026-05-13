using System.Text;
using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Options;
using RW.Discussion.DTOs;
using RW.Discussion.Services;

namespace RW.Discussion.Kafka;

public class KafkaRequestConsumer : BackgroundService
{
    private readonly IServiceScopeFactory _scopeFactory;
    private readonly KafkaSettings _settings;
    private readonly ILogger<KafkaRequestConsumer> _logger;
    private IProducer<string, string>? _producer;

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public KafkaRequestConsumer(
        IServiceScopeFactory scopeFactory,
        IOptions<KafkaSettings> settings,
        ILogger<KafkaRequestConsumer> logger)
    {
        _scopeFactory = scopeFactory;
        _settings = settings.Value;
        _logger = logger;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() => ConsumeLoop(stoppingToken), stoppingToken);
    }

    private async Task ConsumeLoop(CancellationToken stoppingToken)
    {
        var producerConfig = new ProducerConfig
        {
            BootstrapServers = _settings.BootstrapServers,
            Acks = Acks.All,
            EnableIdempotence = true
        };
        _producer = new ProducerBuilder<string, string>(producerConfig).Build();

        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = _settings.BootstrapServers,
            GroupId = _settings.GroupId,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = true
        };

        using var consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        consumer.Subscribe(_settings.InTopic);
        _logger.LogInformation("Discussion consumer subscribed to {Topic}", _settings.InTopic);

        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                ConsumeResult<string, string>? result = null;
                try
                {
                    result = consumer.Consume(stoppingToken);
                }
                catch (OperationCanceledException) { break; }
                catch (ConsumeException ex)
                {
                    _logger.LogError(ex, "Error consuming InTopic");
                    continue;
                }

                if (result?.Message is null) continue;

                var correlationHeader = result.Message.Headers
                    .FirstOrDefault(h => h.Key == "correlationId");
                var replyTopicHeader = result.Message.Headers
                    .FirstOrDefault(h => h.Key == "replyTopic");

                var correlationId = correlationHeader is not null
                    ? Encoding.UTF8.GetString(correlationHeader.GetValueBytes())
                    : Guid.NewGuid().ToString();

                var replyTopic = replyTopicHeader is not null
                    ? Encoding.UTF8.GetString(replyTopicHeader.GetValueBytes())
                    : _settings.OutTopic;

                KafkaResponseEnvelope response;
                try
                {
                    response = await DispatchAsync(result.Message.Value, stoppingToken);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error processing Kafka request");
                    response = new KafkaResponseEnvelope
                    {
                        Status = 500,
                        Error = ex.Message
                    };
                }

                var responseJson = JsonSerializer.Serialize(response, JsonOptions);
                var replyMessage = new Message<string, string>
                {
                    Key = result.Message.Key,
                    Value = responseJson,
                    Headers = new Headers
                    {
                        new Header("correlationId", Encoding.UTF8.GetBytes(correlationId))
                    }
                };

                try
                {
                    await _producer.ProduceAsync(replyTopic, replyMessage, stoppingToken);
                    _logger.LogInformation("Sent response to {Topic} (correlationId={CorrelationId}, status={Status})",
                        replyTopic, correlationId, response.Status);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Failed to produce response to {Topic}", replyTopic);
                }
            }
        }
        finally
        {
            consumer.Close();
            _producer.Flush(TimeSpan.FromSeconds(5));
            _producer.Dispose();
            _producer = null;
        }
    }

    private async Task<KafkaResponseEnvelope> DispatchAsync(string rawValue, CancellationToken ct)
    {
        var envelope = JsonSerializer.Deserialize<KafkaRequestEnvelope>(rawValue, JsonOptions);
        if (envelope is null)
            return new KafkaResponseEnvelope { Status = 400, Error = "Invalid request envelope" };

        using var scope = _scopeFactory.CreateScope();
        var noteService = scope.ServiceProvider.GetRequiredService<INoteService>();

        switch (envelope.Method)
        {
            case KafkaMethods.GetAll:
            {
                var data = await noteService.GetAllAsync();
                return new KafkaResponseEnvelope { Status = 200, Data = data };
            }
            case KafkaMethods.GetById:
            {
                if (envelope.Payload is null)
                    return new KafkaResponseEnvelope { Status = 400, Error = "Missing payload" };
                var id = ExtractId(envelope.Payload.Value);
                var data = await noteService.GetByIdAsync(id);
                if (data is null)
                    return new KafkaResponseEnvelope { Status = 404, Error = $"Note with id {id} was not found." };
                return new KafkaResponseEnvelope { Status = 200, Data = data };
            }
            case KafkaMethods.Create:
            {
                if (envelope.Payload is null)
                    return new KafkaResponseEnvelope { Status = 400, Error = "Missing payload" };
                var dto = JsonSerializer.Deserialize<NoteRequestTo>(envelope.Payload.Value.GetRawText(), JsonOptions);
                if (dto is null)
                    return new KafkaResponseEnvelope { Status = 400, Error = "Invalid payload" };
                if (string.IsNullOrEmpty(dto.Content) || dto.Content.Length < 2 || dto.Content.Length > 2048)
                    return new KafkaResponseEnvelope { Status = 403, Error = "Content must be between 2 and 2048 characters." };

                var created = await noteService.CreateAsync(dto);
                return new KafkaResponseEnvelope { Status = 201, Data = created };
            }
            case KafkaMethods.Update:
            {
                if (envelope.Payload is null)
                    return new KafkaResponseEnvelope { Status = 400, Error = "Missing payload" };
                var dto = JsonSerializer.Deserialize<NoteRequestTo>(envelope.Payload.Value.GetRawText(), JsonOptions);
                if (dto is null)
                    return new KafkaResponseEnvelope { Status = 400, Error = "Invalid payload" };
                if (string.IsNullOrEmpty(dto.Content) || dto.Content.Length < 2 || dto.Content.Length > 2048)
                    return new KafkaResponseEnvelope { Status = 403, Error = "Content must be between 2 and 2048 characters." };

                var updated = await noteService.UpdateAsync(dto);
                if (updated is null)
                    return new KafkaResponseEnvelope { Status = 404, Error = $"Note with id {dto.Id} was not found." };
                return new KafkaResponseEnvelope { Status = 200, Data = updated };
            }
            case KafkaMethods.Delete:
            {
                if (envelope.Payload is null)
                    return new KafkaResponseEnvelope { Status = 400, Error = "Missing payload" };
                var id = ExtractId(envelope.Payload.Value);
                var deleted = await noteService.DeleteAsync(id);
                if (!deleted)
                    return new KafkaResponseEnvelope { Status = 404, Error = $"Note with id {id} was not found." };
                return new KafkaResponseEnvelope { Status = 204 };
            }
            default:
                return new KafkaResponseEnvelope { Status = 400, Error = $"Unknown method: {envelope.Method}" };
        }
    }

    private static long ExtractId(JsonElement payload)
    {
        if (payload.ValueKind == JsonValueKind.Number)
            return payload.GetInt64();

        if (payload.ValueKind == JsonValueKind.Object &&
            payload.TryGetProperty("id", out var idElement))
            return idElement.GetInt64();

        throw new ArgumentException("Cannot extract id from payload");
    }
}
