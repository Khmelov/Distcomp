using Confluent.Kafka;
using System.Text.Json;

namespace Publisher.Service {
    public class KafkaResponseListener : BackgroundService {
        private readonly KafkaService _kafkaService;
        private readonly ILogger<KafkaResponseListener> _logger;

        public KafkaResponseListener(KafkaService kafkaService, ILogger<KafkaResponseListener> logger) {
            _kafkaService = kafkaService;
            _logger = logger;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken) {
            var config = new ConsumerConfig {
                BootstrapServers = "localhost:9092",
                GroupId = "publisher-response-group",
                AutoOffsetReset = AutoOffsetReset.Latest
            };

            using var consumer = new ConsumerBuilder<string, string>(config).Build();
            consumer.Subscribe("OutTopic");

            _logger.LogInformation("Publisher начал слушать OutTopic...");

            await Task.Run(() => {
                while (!stoppingToken.IsCancellationRequested) {
                    try {
                        var result = consumer.Consume(stoppingToken);

                        var jsonDoc = JsonDocument.Parse(result.Message.Value);

                        string correlationId = result.Message.Key;
                        string content = result.Message.Value;

                        _kafkaService.SetResponse(correlationId, content);
                    }
                    catch (Exception ex) {
                        _logger.LogError($"Ошибка чтения из OutTopic: {ex.Message}");
                    }
                }
            }, stoppingToken);
        }
    }
}