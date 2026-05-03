using Confluent.Kafka;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed; 
using Publisher.Data;
using Publisher.Dtos;
using Publisher.Dtos.Kafka;
using Publisher.Services;
using System.Text.Json;
using Publisher.Common;

namespace Publisher.Repositories
{
    public class KafkaReactionRepository
    {
        private readonly IProducer<string, string> _producer;
        private readonly KafkaResponseTracker _tracker;
        private readonly AppDbContext _context;
        private readonly IDistributedCache _cache;
        private readonly string _inTopic = "InTopic";

        public KafkaReactionRepository(KafkaResponseTracker tracker, AppDbContext context, IDistributedCache cache)
        {
            _tracker = tracker;
            _context = context;
            _cache = cache; 
            var config = new ProducerConfig { BootstrapServers = "localhost:9092" };
            _producer = new ProducerBuilder<string, string>(config).Build();
        }

        private async Task<long> GetNextIdFromPostgres()
        {
            using var command = _context.Database.GetDbConnection().CreateCommand();
            command.CommandText = "SELECT nextval('distcomp.tbl_reaction_id_seq')";

            if (command.Connection.State != System.Data.ConnectionState.Open)
                await command.Connection.OpenAsync();

            var result = await command.ExecuteScalarAsync();
            return Convert.ToInt64(result);
        }

        // --- CREATE ---
        public async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request)
        {
            long realId = await GetNextIdFromPostgres();

            var result = new ReactionResponseTo
            {
                Id = realId,
                TopicId = request.TopicId,
                Content = request.Content,
                State = "PENDING"
            };

            var msg = new KafkaMessage { Method = "CREATE", Payload = JsonSerializer.SerializeToElement(result) };
            await _producer.ProduceAsync(_inTopic, new Message<string, string>
            {
                Key = result.TopicId.ToString(),
                Value = JsonSerializer.Serialize(msg)
            });

            await _cache.RemoveAsync("reactions_all");

            return result;
        }

        // --- GET BY ID ---
        public async Task<ReactionResponseTo?> FindByIdAsync(long id)
        {
            string cacheKey = $"reaction_{id}";

            // 1. Пытаемся взять из кэша
            var cachedData = await _cache.GetStringAsync(cacheKey);
            if (!string.IsNullOrEmpty(cachedData))
            {
                return JsonSerializer.Deserialize<ReactionResponseTo>(cachedData);
            }

            // 2. Если в кэше нет — идем в Kafka
            var reaction = await SendRequestAsync<ReactionResponseTo>("FIND_BY_ID", id, id.ToString());

            // 3. Если получили ответ — сохраняем в кэш на 5 минут
            if (reaction != null)
            {
                var options = new DistributedCacheEntryOptions().SetAbsoluteExpiration(TimeSpan.FromMinutes(5));
                await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(reaction), options);
            }

            return reaction;
        }

        // --- GET ALL ---
        public async Task<IEnumerable<ReactionResponseTo>> GetAllAsync()
        {
            string cacheKey = "reactions_all";

            // 1. Проверяем кэш всего списка
            var cachedData = await _cache.GetStringAsync(cacheKey);
            if (!string.IsNullOrEmpty(cachedData))
            {
                return JsonSerializer.Deserialize<List<ReactionResponseTo>>(cachedData) ?? new List<ReactionResponseTo>();
            }

            // 2. Запрашиваем через Kafka
            var results = await SendRequestAsync<List<ReactionResponseTo>>("GET_ALL", null, "all-key");

            // 3. Сохраняем результат в кэш
            if (results != null)
            {
                var options = new DistributedCacheEntryOptions().SetAbsoluteExpiration(TimeSpan.FromMinutes(1));
                await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(results), options);
            }

            return results ?? new List<ReactionResponseTo>();
        }

        // --- DELETE ---
        public async Task<ReactionResponseTo?> UpdateAsync(ReactionRequestTo request)
        {
            var updated = await SendRequestAsync<ReactionResponseTo>("UPDATE", request, request.Id.ToString());
            if (updated != null)
            {
                await _cache.RemoveAsync(CacheKeys.Entity("reaction", updated.Id));
                await _cache.RemoveAsync(CacheKeys.List("reaction"));
                // ТРЕБОВАНИЕ: Изменение Reaction очищает topic -> reaction
                await _cache.RemoveAsync(CacheKeys.ReactionsByTopic(updated.TopicId));
            }
            return updated;
        }

        public async Task DeleteAsync(long id)
        {
            // 1. Пытаемся получить реакцию, чтобы узнать topicId до удаления
            // Используем уже существующий метод FindByIdAsync (он проверит и кэш, и Kafka)
            var reaction = await FindByIdAsync(id);

            // 2. Отправляем запрос на удаление в Kafka
            await SendRequestAsync<object>("DELETE", id, id.ToString());

            // 3. Инвалидация самой реакции и общего списка
            await _cache.RemoveAsync(CacheKeys.Entity("reaction", id));
            await _cache.RemoveAsync(CacheKeys.List("reaction"));

            // 4. Инвалидация связи topic -> reaction (если удалось найти topicId)
            if (reaction != null)
            {
                await _cache.RemoveAsync(CacheKeys.ReactionsByTopic(reaction.TopicId));
            }
        }

        private async Task<T?> SendRequestAsync<T>(string method, object? payload, string kafkaKey)
        {
            var correlationId = Guid.NewGuid().ToString();
            var waitTask = _tracker.WaitForResponse(correlationId);

            var msg = new KafkaMessage
            {
                Method = method,
                CorrelationId = correlationId,
                Payload = JsonSerializer.SerializeToElement(payload)
            };

            await _producer.ProduceAsync(_inTopic, new Message<string, string>
            {
                Key = kafkaKey,
                Value = JsonSerializer.Serialize(msg)
            });

           
            if (await Task.WhenAny(waitTask, Task.Delay(2000)) == waitTask)
            {
                var responseJson = await waitTask;
                if (string.IsNullOrEmpty(responseJson) || responseJson == "null")
                    return default;

                return JsonSerializer.Deserialize<T>(responseJson, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
            }

            _tracker.CancelWait(correlationId);
            throw new TimeoutException("504 Gateway Timeout");
        }
    }
}