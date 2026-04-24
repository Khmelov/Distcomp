using Confluent.Kafka;
using System.Text.Json;
using Microsoft.Extensions.Configuration;
using DiscussionService.Models;

namespace DiscussionService.Kafka
{
    public interface IKafkaProducer
    {
        Task SendStatusUpdateAsync(long postId, PostState newState);
    }

    public class KafkaProducer : IKafkaProducer, IDisposable
    {
        private readonly IProducer<string, string> _producer;
        private readonly string _outTopic;

        public KafkaProducer(IConfiguration configuration)
        {
            var config = new ProducerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"]
            };
            _producer = new ProducerBuilder<string, string>(config).Build();
            _outTopic = configuration["Kafka:OutTopic"] ?? "OutTopic";
        }

        public async Task SendStatusUpdateAsync(long postId, PostState newState)
        {
            var key = postId.ToString();   // ключ может быть любым, но для порядка используем postId
            var value = JsonSerializer.Serialize(new
            {
                PostId = postId,
                NewState = newState.ToString()
            });

            await _producer.ProduceAsync(_outTopic, new Message<string, string>
            {
                Key = key,
                Value = value
            });
        }

        public void Dispose()
        {
            _producer?.Dispose();
        }
    }
}