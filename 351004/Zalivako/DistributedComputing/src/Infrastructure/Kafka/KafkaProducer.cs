using Application.Interfaces;
using Confluent.Kafka;
using Core.Entities;
using Microsoft.Extensions.Configuration;
using System.Text.Json;

namespace Infrastructure.Kafka
{
    public class KafkaProducer : IKafkaProducer, IDisposable
    {
        private readonly IProducer<string, string> _producer;
        private readonly string _inTopic;

        public KafkaProducer(IConfiguration configuration)
        {
            var config = new ProducerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092"
            };

            _producer = new ProducerBuilder<string, string>(config).Build();
            _inTopic = configuration["Kafka:InTopic"] ?? "InTopic";
        }

        public async Task SendPostAsync(Post post)
        {
            // Ключ сообщения = NewsId, чтобы все посты одной новости попадали в одну партицию
            var key = post.NewsId.ToString();

            // Сериализуем пост в JSON
            var value = JsonSerializer.Serialize(new PostMessage
            {
                Id = post.Id,
                NewsId = post.NewsId,
                Content = post.Content,
                Country = post.Country,
                State = post.State.ToString()
            });

            var message = new Message<string, string>
            {
                Key = key,
                Value = value
            };

            await _producer.ProduceAsync(_inTopic, message);
        }

        public void Dispose()
        {
            _producer?.Dispose();
        }

        // Вспомогательный класс для сериализации
        private class PostMessage
        {
            public long Id { get; set; }
            public long NewsId { get; set; }
            public string Content { get; set; } = string.Empty;
            public string Country { get; set; } = string.Empty;
            public string State { get; set; } = "PENDING";
        }
    }
}