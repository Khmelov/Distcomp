using Confluent.Kafka;
using Publisher.Dtos;
using Publisher.Services;
using System.Collections.Concurrent;
using System.Text.Json;

namespace Publisher.Proxies
{
    public class ReactionProxy : IReactionProxy
    {
        private readonly HttpClient _httpClient;
        private readonly IKafkaProducer _kafkaProducer;
        private static readonly ConcurrentDictionary<long, TaskCompletionSource<ReactionResponseTo>> _pending = new();
        private long _nextId = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();

        public ReactionProxy(IHttpClientFactory httpClientFactory, IKafkaProducer kafkaProducer)
        {
            _httpClient = httpClientFactory.CreateClient("DiscussionClient");
            _kafkaProducer = kafkaProducer;
        }

        private long GenerateId() => Interlocked.Increment(ref _nextId);

        // --- Асинхронное создание через Kafka ---
        public async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request)
        {
            var id = GenerateId();
            var reaction = new ReactionResponseTo
            {
                Id = id,
                TopicId = request.TopicId,
                Content = request.Content,
                State = "PENDING"
            };
            var tcs = new TaskCompletionSource<ReactionResponseTo>();
            _pending[id] = tcs;

            await _kafkaProducer.ProduceAsync("InTopic", request.TopicId.ToString(), reaction);

            var timeout = Task.Delay(5000);
            var completed = await Task.WhenAny(tcs.Task, timeout);
            if (completed == timeout)
                throw new TimeoutException("Модерация не завершена");
            return await tcs.Task;
        }

        // --- Остальные методы (синхронные через HTTP) ---
        public async Task<ReactionResponseTo> GetByIdAsync(long topicId, long id)
        {
            var response = await _httpClient.GetAsync($"/api/v1.0/reactions/{topicId}/{id}");
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }

        public async Task<IEnumerable<ReactionResponseTo>> GetByTopicIdAsync(long topicId)
        {
            var response = await _httpClient.GetAsync($"/api/v1.0/reactions?topicId={topicId}");
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<IEnumerable<ReactionResponseTo>>();
        }

        public async Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request)
        {
            var response = await _httpClient.PutAsJsonAsync("/api/v1.0/reactions", request);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }

        public async Task DeleteAsync(long topicId, long id)
        {
            var response = await _httpClient.DeleteAsync($"/api/v1.0/reactions/{topicId}/{id}");
            response.EnsureSuccessStatusCode();
        }

        public async Task<ReactionResponseTo> GetByIdOnlyAsync(long id)
        {
            var response = await _httpClient.GetAsync($"/api/v1.0/reactions/{id}");
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }

        // --- Статический метод для завершения ожидания из OutTopicConsumer ---
        public static bool TryCompletePending(long id, ReactionResponseTo reaction)
        {
            if (_pending.TryRemove(id, out var tcs))
            {
                tcs.SetResult(reaction);
                return true;
            }
            return false;
        }
    }
}