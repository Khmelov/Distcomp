using Confluent.Kafka;
using System.Text.Json;
using System.Threading.Tasks;
using WebApplication1.DTO;

namespace WebApplication1.Service
{
    public class KafkaReactionProducerService
    {
        private readonly IProducer<string, string> _producer;

        public KafkaReactionProducerService()
        {
            var config = new ProducerConfig { BootstrapServers = "localhost:9092", SecurityProtocol = SecurityProtocol.Plaintext };
            _producer = new ProducerBuilder<string, string>(config).Build();
        }

        public async Task SendReactionAsync(ReactionRequestTo dto)
        {
            string key = dto.TopicId.ToString();
            string message = JsonSerializer.Serialize(dto);
            await _producer.ProduceAsync("InTopic", new Message<string, string> { Key = key, Value = message });
        }
    }
}
