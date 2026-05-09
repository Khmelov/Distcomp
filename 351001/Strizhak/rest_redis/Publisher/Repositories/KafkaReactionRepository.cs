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
            var config = new ProducerConfig
            {
                BootstrapServers = "localhost:9092",
                LingerMs = 0,
                Acks = Acks.Leader
            };
            _producer = new ProducerBuilder<string, string>(config).Build();
        }

        private async Task<long> GetNextIdFromPostgres()
        {
            using var command = _context.Database.GetDbConnection().CreateCommand();
            command.CommandText = "SELECT nextval('distcomp.tbl_reaction_id_seq')";
            if (command.Connection.State != System.Data.ConnectionState.Open)
                await command.Connection.OpenAsync();
            return Convert.ToInt64(await command.ExecuteScalarAsync());
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

            
            var options = new DistributedCacheEntryOptions().SetAbsoluteExpiration(TimeSpan.FromMinutes(5));
            await _cache.SetStringAsync(CacheKeys.Entity("reaction", realId), JsonSerializer.Serialize(result), options);

            // ИНВАЛИДАЦИЯ - используем строго CacheKeys
            await InvalidateLists(result.TopicId);

            return result;
        }

        // --- GET BY ID ---
        public async Task<ReactionResponseTo?> FindByIdAsync(long id)
        {
            // Исправлено: используем CacheKeys вместо "reaction_{id}"
            string cacheKey = CacheKeys.Entity("reaction", id);
            var cachedData = await _cache.GetStringAsync(cacheKey);

            if (!string.IsNullOrEmpty(cachedData))
                return JsonSerializer.Deserialize<ReactionResponseTo>(cachedData);

            var reaction = await SendRequestAsync<ReactionResponseTo>("FIND_BY_ID", id, id.ToString());

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
            
            string cacheKey = CacheKeys.List("reaction");
            var cachedData = await _cache.GetStringAsync(cacheKey);

            if (!string.IsNullOrEmpty(cachedData))
                return JsonSerializer.Deserialize<List<ReactionResponseTo>>(cachedData) ?? new List<ReactionResponseTo>();

            var results = await SendRequestAsync<List<ReactionResponseTo>>("GET_ALL", null, "all-key");

            if (results != null)
            {
                var options = new DistributedCacheEntryOptions().SetAbsoluteExpiration(TimeSpan.FromMinutes(1));
                await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(results), options);
            }
            return results ?? new List<ReactionResponseTo>();
        }

        // --- UPDATE ---
        public async Task<ReactionResponseTo?> UpdateAsync(ReactionRequestTo request)
        {
            var updated = await SendRequestAsync<ReactionResponseTo>("UPDATE", request, request.Id.ToString());
            if (updated != null)
            {
                await _cache.RemoveAsync(CacheKeys.Entity("reaction", updated.Id));
                await InvalidateLists(updated.TopicId);
            }
            return updated;
        }

        // --- DELETE ---
        public async Task DeleteAsync(long id)
        {
            var existing = await FindByIdAsync(id);

            await SendRequestAsync<object>("DELETE", id, id.ToString());

            await _cache.RemoveAsync(CacheKeys.Entity("reaction", id));
            if (existing != null)
            {
                await InvalidateLists(existing.TopicId);
            }
            else
            {
                await _cache.RemoveAsync(CacheKeys.List("reaction"));
            }
        }

        // Вспомогательный метод инвалидации списков
        private async Task InvalidateLists(long topicId)
        {
            await _cache.RemoveAsync(CacheKeys.List("reaction"));
            await _cache.RemoveAsync(CacheKeys.ReactionsByTopic(topicId));
            await _cache.RemoveAsync("reactions_all");
            await _cache.RemoveAsync("reaction:all");
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