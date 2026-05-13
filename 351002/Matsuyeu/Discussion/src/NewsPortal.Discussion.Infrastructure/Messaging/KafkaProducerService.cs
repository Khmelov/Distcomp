using System.Text.Json;
using Confluent.Kafka;
using Discussion.src.NewsPortal.Discussion.Domain.Entities;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Messaging
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
                    Key = message.Data.NewsId.ToString(), //Гарантия попадания в одну партицию
                    Value = json
                };

                var result = await _producer.ProduceAsync(topic, kafkaMessage);

                _logger.LogInformation(
                    "Message sent to {Topic}, Partition: {Partition}, Offset: {Offset}, NewsId: {NewsId}",
                    result.Topic, result.Partition.Value, result.Offset.Value, message.Data.NewsId);
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
