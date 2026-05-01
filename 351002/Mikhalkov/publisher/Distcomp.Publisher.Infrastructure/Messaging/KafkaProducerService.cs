using Confluent.Kafka;
using Distcomp.Shared.Models;
using Microsoft.Extensions.Configuration;
using System.Text.Json;

namespace Distcomp.Infrastructure.Messaging
{
    public class KafkaProducerService
    {
        private readonly IConfiguration _config;
        private readonly IProducer<string, string> _producer;

        public KafkaProducerService(IConfiguration config)
        {
            _config = config;
            var producerConfig = new ProducerConfig
            {
                BootstrapServers = _config.GetValue<string>("Kafka:BootstrapServers") ?? "localhost:9092"
            };
            _producer = new ProducerBuilder<string, string>(producerConfig).Build();
        }

        public async Task SendNoteAsync(Note note)
        {
            var topic = "InTopic";
            var messageValue = JsonSerializer.Serialize(note);

            var message = new Message<string, string>
            {
                Key = note.IssueId.ToString(),
                Value = messageValue
            };

            await _producer.ProduceAsync(topic, message);
        }
    }
}