using Confluent.Kafka;
using Discussion.DTO;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Discussion.Service {
    public class KafkaService : BackgroundService {
        private readonly IServiceProvider _serviceProvider;
        private readonly ILogger<KafkaService> _logger;
        private readonly string _bootstrapServers = "localhost:9092";

        public KafkaService(IServiceProvider serviceProvider, ILogger<KafkaService> logger) {
            _serviceProvider = serviceProvider;
            _logger = logger;
        }

        protected override async Task ExecuteAsync(CancellationToken ct) {
            var config = new ConsumerConfig {
                BootstrapServers = _bootstrapServers,
                GroupId = "discussion-group",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };

            using var consumer = new ConsumerBuilder<string, string>(config).Build();
            consumer.Subscribe("InTopic");

            var pConfig = new ProducerConfig { BootstrapServers = _bootstrapServers };
            using var producer = new ProducerBuilder<string, string>(pConfig).Build();

            while (!ct.IsCancellationRequested) {
                try {
                    var result = consumer.Consume(ct);
                    var options = new JsonSerializerOptions {
                        PropertyNameCaseInsensitive = true,
                        Converters = { new JsonStringEnumConverter() }
                    };

                    var kafkaMsg = JsonSerializer.Deserialize<KafkaMessage>(result.Message.Value, options);

                    if (kafkaMsg?.Data == null) {
                        _logger.LogError("Data is null after deserialization!");
                        return;
                    }

                    using (var scope = _serviceProvider.CreateScope()) {
                        var service = scope.ServiceProvider.GetRequiredService<ReactionService>();
                        string responsePayload = "";

                        switch (kafkaMsg.Operation) {
                            case "POST":
                                if (kafkaMsg.Data.Content != null) {
                                    kafkaMsg.Data.State = kafkaMsg.Data.Content.Contains("bad")
                                        ? ReactionState.DECLINE
                                        : ReactionState.APPROVE;

                                    // СОХРАНЯЕМ В БАЗУ
                                    var createdReaction = await service.CreateAsync(kafkaMsg.Data);
                                    // Формируем ответ для OutTopic
                                    responsePayload = JsonSerializer.Serialize(createdReaction);
                                }
                                break;
                            case "GET_BY_ID":
                            case "GET_BY_ID_ONLY":
                                var results = await service.FindByIdAsync(kafkaMsg.Data.Id);
                                var found = results.FirstOrDefault();
                                responsePayload = found != null ? JsonSerializer.Serialize(found) : "{}";
                                break;
                            case "GET_BY_TWEET":
                                var list = await service.GetByTweetIdAsync(kafkaMsg.Data.TweetId, kafkaMsg.Data.Country);
                                responsePayload = JsonSerializer.Serialize(list);
                                break;
                            case "GET_ALL":
                                var all = await service.GetAllAsync();
                                responsePayload = JsonSerializer.Serialize(all);
                                break;
                            case "PUT":
                                var updated = await service.UpdateAsync(kafkaMsg.Data);
                                responsePayload = JsonSerializer.Serialize(updated);
                                break;
                            case "DELETE":
                                var deleted = await service.DeleteAsync(kafkaMsg.Data.Country, kafkaMsg.Data.TweetId, kafkaMsg.Data.Id);
                                responsePayload = JsonSerializer.Serialize(new { success = deleted });
                                break;

                            case "PUT_BY_ID":
                                // Используем настройки с CamelCase и Enum как строки/числа (по вашему конфигу)
                                var jsonOptions = new JsonSerializerOptions {
                                    PropertyNamingPolicy = JsonNamingPolicy.CamelCase
                                };

                                var putData = JsonSerializer.Deserialize<JsonElement>(JsonSerializer.Serialize(kafkaMsg.Data));
                                long updateId = putData.GetProperty("Id").GetInt64();

                                // ПАРСИМ входящий контент, чтобы достать только текст
                                string rawJson = putData.GetProperty("Content").GetString();
                                string actualContent = "";
                                using (JsonDocument doc = JsonDocument.Parse(rawJson)) {
                                    if (doc.RootElement.TryGetProperty("content", out var contentProp)) {
                                        actualContent = contentProp.GetString();
                                    }
                                }

                                var reactions = await service.FindByIdAsync(updateId);
                                var foundDto = reactions.FirstOrDefault();

                                if (foundDto != null) {
                                    var updateRequest = new ReactionRequestTo {
                                        Id = foundDto.Id,
                                        TweetId = foundDto.TweetId,
                                        Country = foundDto.Country,
                                        Content = actualContent, // Теперь здесь только "updatedcontent2839"
                                        State = actualContent.Contains("bad") ? ReactionState.DECLINE : ReactionState.APPROVE
                                    };

                                    await service.UpdateAsync(updateRequest);

                                    // ВАЖНО: Сериализуем с CamelCase для теста
                                    responsePayload = JsonSerializer.Serialize(updateRequest, jsonOptions);
                                }
                                else {
                                    responsePayload = "{}";
                                }
                                break;

                            case "DELETE_BY_ID":
                                var delData = JsonSerializer.Deserialize<JsonElement>(JsonSerializer.Serialize(kafkaMsg.Data));
                                long deleteId = delData.GetProperty("Id").GetInt64();

                                var reactionsToDelete = await service.FindByIdAsync(deleteId);
                                foreach (var r in reactionsToDelete) {
                                    await service.DeleteAsync(r.Country, r.TweetId, r.Id);
                                }
                                responsePayload = "{\"success\": true}";
                                break;
                        }

                        await producer.ProduceAsync("OutTopic", new Message<string, string> {
                            Key = kafkaMsg.CorrelationId,
                            Value = responsePayload
                        });
                    }
                }
                catch (Exception ex) {
                    _logger.LogError($"Error processing Kafka message: {ex.Message} | Стек: {ex.StackTrace}");
                }
            }
        }
    }
}
