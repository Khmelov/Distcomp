using System.Net.Http.Json;
using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Options;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models;
using RestApiTask.Models.DTOs;
using RestApiTask.Services.Interfaces;
using RestApiTask.Repositories;

namespace RestApiTask.Services;

public class RemoteMessageService : IMessageService
{
    private readonly HttpClient _http;
    private readonly ICacheService _cache;
    private readonly ProducerConfig _producerConfig;
    private readonly string _topic;
    private readonly ILogger<RemoteMessageService> _logger;
    private const string BasePath = "api/v1.0/messages";
    private const string CacheKeyPrefix = "message:";
    private const string CacheKeyAll = "messages:all";

    public RemoteMessageService(
        HttpClient http,
        ICacheService cache,
        IOptions<KafkaSettings> options,
        ILogger<RemoteMessageService> logger)
    {
        _http = http;
        _cache = cache;
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

    public async Task<IEnumerable<MessageResponseTo>> GetAllAsync(QueryOptions? options = null)
    {
        if (options is null)
        {
            var cached = await _cache.GetAsync<IEnumerable<MessageResponseTo>>(CacheKeyAll);
            if (cached != null)
            {
                return cached;
            }
        }

        var response = await _http.GetAsync(BasePath);
        if (!response.IsSuccessStatusCode)
        {
            throw new HttpRequestException($"Unexpected status code: {(int)response.StatusCode}");
        }

        var data = await response.Content.ReadFromJsonAsync<IEnumerable<MessageResponseTo>>() ?? new List<MessageResponseTo>();
        if (options is null)
        {
            await _cache.SetAsync(CacheKeyAll, data);
        }
        return data;
    }

    public async Task<MessageResponseTo> GetByIdAsync(long id)
    {
        var cacheKey = CacheKeyPrefix + id;
        var cached = await _cache.GetAsync<MessageResponseTo>(cacheKey);
        if (cached != null)
        {
            return cached;
        }

        var response = await _http.GetAsync($"{BasePath}/{id}");
        if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            throw new NotFoundException("Message not found");
        }

        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<MessageResponseTo>()
                     ?? throw new InvalidOperationException("Discussion API returned empty response.");
        await _cache.SetAsync(cacheKey, result);
        return result;
    }

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

        var created = new MessageResponseTo
        {
            Id = message.Id,
            ArticleId = message.ArticleId,
            Content = message.Content,
            CreatedAt = message.CreatedAt
        };

        await _cache.SetAsync(CacheKeyPrefix + created.Id, created);
        await _cache.RemoveAsync(CacheKeyAll);
        return created;
    }

    public async Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request)
    {
        var resp = await _http.PutAsJsonAsync($"{BasePath}/{id}", request);
        if (resp.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            throw new NotFoundException("Message not found");
        }
        resp.EnsureSuccessStatusCode();
        var updated = await resp.Content.ReadFromJsonAsync<MessageResponseTo>()
                      ?? throw new InvalidOperationException("Discussion API returned empty response.");
        await _cache.SetAsync(CacheKeyPrefix + id, updated);
        await _cache.RemoveAsync(CacheKeyAll);
        return updated;
    }

    public async Task DeleteAsync(long id)
    {
        var resp = await _http.DeleteAsync($"{BasePath}/{id}");
        if (resp.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            throw new NotFoundException("Message not found");
        }
        resp.EnsureSuccessStatusCode();
        await _cache.RemoveAsync(CacheKeyPrefix + id);
        await _cache.RemoveAsync(CacheKeyAll);
    }

    private sealed class KafkaMessage
    {
        public long Id { get; set; }
        public long ArticleId { get; set; }
        public string Content { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; }
    }
}