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
                                }
                                break;
                            case "GET_BY_ID":
                            case "GET_BY_ID_ONLY":
                                var found = await service.GetByIdAsync(kafkaMsg.Data.Country, kafkaMsg.Data.TweetId, kafkaMsg.Data.Id);
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
