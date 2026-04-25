using Confluent.Kafka;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using Publisher.Data;
using Publisher.Dtos;
using Publisher.Dtos.Kafka;
using Publisher.Services;
using System.Text.Json;

namespace Publisher.Repositories
{
    public class KafkaReactionRepository
    {
        private readonly IProducer<string, string> _producer;
        private readonly KafkaResponseTracker _tracker;
        private readonly string _inTopic = "InTopic";
        private long _nextId = 0;
        private readonly AppDbContext _context;
        private long GenerateId() => Interlocked.Increment(ref _nextId);
        public KafkaReactionRepository(KafkaResponseTracker tracker, AppDbContext context)
        {
            _tracker = tracker;
            var config = new ProducerConfig { BootstrapServers = "localhost:9092" };
            _producer = new ProducerBuilder<string, string>(config).Build();
            _context = context;
        }
        private async Task<long> GetNextIdFromPostgres()
        {
            // Используем EF Core для выполнения команды получения следующего значения
            using var command = _context.Database.GetDbConnection().CreateCommand();
            command.CommandText = "SELECT nextval('distcomp.tbl_reaction_id_seq')";

            if (command.Connection.State != System.Data.ConnectionState.Open)
                await command.Connection.OpenAsync();

            var result = await command.ExecuteScalarAsync();
            return Convert.ToInt64(result);
        }

        // --- CREATE (Fire-and-forget по ТЗ) ---
        public async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request)
        {
            // 1. Берем честный ID из Postgres
            long realId = await GetNextIdFromPostgres();

            // 2. Создаем НОВЫЙ объект ответа, в который ПИШЕМ этот ID
            var result = new ReactionResponseTo
            {
                Id = realId, // Теперь тут 1
                TopicId = request.TopicId,
                Content = request.Content,
                State = "PENDING"
            };

            // 3. В Кафку шлем этот же объект result (чтобы Discussion тоже получил Id=1)
            var msg = new KafkaMessage { Method = "CREATE", Payload = JsonSerializer.SerializeToElement(result) };
            await _producer.ProduceAsync(_inTopic, new Message<string, string>
            {
                Key = result.TopicId.ToString(),
                Value = JsonSerializer.Serialize(msg)
            });

            // 4. Возвращаем КЛИЕНТУ объект с Id=1
            return result;
        }


        // --- GET BY ID (Блокирующий) ---
        public async Task<ReactionResponseTo?> FindByIdAsync(long id)
        {
            return await SendRequestAsync<ReactionResponseTo>("FIND_BY_ID", id, id.ToString());
        }

        // --- GET ALL (Блокирующий) ---
        public async Task<IEnumerable<ReactionResponseTo>> GetAllAsync()
        {
            // Отправляем запрос без конкретного ID в Payload (или передаем 0)
            var results = await SendRequestAsync<List<ReactionResponseTo>>("GET_ALL", null, "all-key");
            return results ?? new List<ReactionResponseTo>();
        }

        // --- DELETE (Блокирующий) ---
        public async Task DeleteAsync(long id)
        {
            // Ждем подтверждения удаления (например, Discussion пришлет "true" или объект)
            await SendRequestAsync<object>("DELETE", id, id.ToString());
        }
        // --- UPDATE (Блокирующий) ---
        public async Task<ReactionResponseTo?> UpdateAsync(ReactionRequestTo request)
        {
            // Отправляем объект целиком, ждем результат обновления
            return await SendRequestAsync<ReactionResponseTo>(
                "UPDATE",
                request,
                request.Id.ToString() // Используем Id как ключ партиции
            );
        }
        // --- Вспомогательный универсальный метод для Request-Response через Kafka ---
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

            // Ждем 2 секунды (Таймаут по ТЗ)
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