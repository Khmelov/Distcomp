using Confluent.Kafka;
using Publisher.DTO;
using System.Collections.Concurrent;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Publisher.Service {
    public class KafkaService {
        private readonly IProducer<string, string> _producer;
        private readonly ConcurrentDictionary<string, TaskCompletionSource<string>> _pendingRequests = new();
        private readonly JsonSerializerOptions _jsonOptions;

        public KafkaService() {
            var config = new ProducerConfig { BootstrapServers = "localhost:9092" };
            _producer = new ProducerBuilder<string, string>(config).Build();

            _jsonOptions = new JsonSerializerOptions {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
                DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull
            };
            _jsonOptions.Converters.Add(new JsonStringEnumConverter());
        }

        public async Task SendMessageAsync(string key, ReactionRequestTo data, string operation, string correlationId) {
            var message = new KafkaMessage {
                Operation = operation,
                CorrelationId = correlationId,
                Data = data
            };

            var val = JsonSerializer.Serialize(message, _jsonOptions);

            await _producer.ProduceAsync("InTopic", new Message<string, string> {
                Key = key,
                Value = val
            });
        }

        public async Task<string> SendAndAwaitAsync(string key, ReactionRequestTo data, string operation) {
            var correlationId = Guid.NewGuid().ToString();
            var tcs = new TaskCompletionSource<string>();

            _pendingRequests[correlationId] = tcs;

            await SendMessageAsync(key, data, operation, correlationId);

            var completedTask = await Task.WhenAny(tcs.Task, Task.Delay(1000));
            if (completedTask == tcs.Task) {
                return await tcs.Task;
            }

            _pendingRequests.TryRemove(correlationId, out _);
            throw new TimeoutException("Время ожидания ответа от модуля Discussion истекло.");
        }

        public void SetResponse(string correlationId, string responseContent) {
            if (_pendingRequests.TryRemove(correlationId, out var tcs)) {
                tcs.SetResult(responseContent);
            }
        }
    }
}