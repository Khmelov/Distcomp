using System.Net.Http.Json;
using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Options;
using RestApiTask.Models;
using RestApiTask.Models.DTOs;
using RestApiTask.Services.Interfaces;
using RestApiTask.Repositories;

namespace RestApiTask.Services;

public class RemoteMessageService : IMessageService
{
    private readonly HttpClient _http;
    private readonly ProducerConfig _producerConfig;
    private readonly string _topic;
    private readonly ILogger<RemoteMessageService> _logger;
    private const string BasePath = "api/v1.0/messages";

    public RemoteMessageService(HttpClient http, IOptions<KafkaSettings> options, ILogger<RemoteMessageService> logger)
    {
        _http = http;
        _logger = logger;
        var kafka = options.Value;
        _topic = kafka.Topic;
        _producerConfig = new ProducerConfig
        {
            BootstrapServers = kafka.BootstrapServers,
            ClientId = kafka.ClientId ?? "publisher-producer",
            Acks = Acks.All,
            EnableIdempotence = true
        };
    }

    public async Task<IEnumerable<MessageResponseTo>> GetAllAsync(QueryOptions? options = null) =>
        await _http.GetFromJsonAsync<IEnumerable<MessageResponseTo>>(BasePath) ?? new List<MessageResponseTo>();

    public async Task<MessageResponseTo> GetByIdAsync(long id) =>
        await _http.GetFromJsonAsync<MessageResponseTo>($"{BasePath}/{id}")
        ?? throw new Exception("Not Found");

    public async Task<MessageResponseTo> CreateAsync(MessageRequestTo request)
    {
        // Message contract sent to Kafka and then persisted by discussion module.
        var message = new KafkaMessage
        {
            Id = DateTime.UtcNow.Ticks,
            ArticleId = request.ArticleId,
            Content = request.Content,
            CreatedAt = DateTime.UtcNow
        };

        var payload = JsonSerializer.Serialize(message);

        try
        {
            using var producer = new ProducerBuilder<long, string>(_producerConfig)
                .SetErrorHandler((_, e) => _logger.LogError("Kafka producer error: {Reason}", e.Reason))
                .Build();

            // ProduceAsync gives delivery guarantee info and throws on broker-side failures.
            var result = await producer.ProduceAsync(_topic, new Confluent.Kafka.Message<long, string>
            {
                Key = message.Id,
                Value = payload
            });

            _logger.LogInformation("Kafka message produced to {TopicPartitionOffset}", result.TopicPartitionOffset);
        }
        catch (ProduceException<long, string> ex)
        {
            _logger.LogError(ex, "Kafka produce failed");
            throw;
        }

        return new MessageResponseTo
        {
            Id = message.Id,
            ArticleId = message.ArticleId,
            Content = message.Content,
            CreatedAt = message.CreatedAt
        };
    }

    public async Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request)
    {
        var resp = await _http.PutAsJsonAsync($"{BasePath}/{id}", request);
        resp.EnsureSuccessStatusCode();
        return await resp.Content.ReadFromJsonAsync<MessageResponseTo>()
               ?? throw new InvalidOperationException("Discussion API returned empty response.");
    }

    public async Task DeleteAsync(long id) => await _http.DeleteAsync($"{BasePath}/{id}");

    private sealed class KafkaMessage
    {
        public long Id { get; set; }
        public long ArticleId { get; set; }
        public string Content { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; }
    }
}