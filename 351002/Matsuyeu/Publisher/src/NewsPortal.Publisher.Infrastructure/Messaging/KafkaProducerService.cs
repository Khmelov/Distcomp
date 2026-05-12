using System.Text.Json;
using Confluent.Kafka;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Messaging
{
    public class KafkaProducerService : IKafkaProducerService, IDisposable
    {
        private readonly IProducer<string, string> _producer;
        private readonly ILogger<KafkaProducerService> _logger;

        public KafkaProducerService(IConfiguration configuration, ILogger<KafkaProducerService> logger)
        {
            _logger = logger;

            var config = new ProducerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092",
                Acks = Acks.All,
                EnableIdempotence = true,
                MaxInFlight = 5,
                CompressionType = CompressionType.Snappy
            };

            _producer = new ProducerBuilder<string, string>(config).Build();
        }

        public async Task SendAsync(string topic, KafkaNoteMessage message)
        {
            try
            {
                var json = JsonSerializer.Serialize(message);
                var kafkaMessage = new Message<string, string>
                {
                    Key = message.Data.NewsId.ToString(),
                    Value = json
                };

                var result = await _producer.ProduceAsync(topic, kafkaMessage);

                _logger.LogInformation(
                    "Message sent to {Topic}, Partition: {Partition}, Offset: {Offset}",
                    result.Topic, result.Partition.Value, result.Offset.Value);
            }
            catch (ProduceException<string, string> ex)
            {
                _logger.LogError(ex, "Failed to send message to Kafka");
                throw;
            }
        }

        public void Dispose()
        {
            _producer?.Flush(TimeSpan.FromSeconds(10));
            _producer?.Dispose();
        }
    }
}
