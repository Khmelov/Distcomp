using System.Text.Json;
using Confluent.Kafka;
using Discussion.Models;

namespace Discussion.Services;

public class KafkaConsumerService : BackgroundService
{
    private readonly CassandraService _db;
    private readonly IConfiguration _config;
    private readonly IProducer<Null, string> _producer;

    public KafkaConsumerService(CassandraService db, IConfiguration config)
    {
        _db = db;
        _config = config;
        var producerConfig = new ProducerConfig { BootstrapServers = config["Kafka:BootstrapServers"] };
        _producer = new ProducerBuilder<Null, string>(producerConfig).Build();
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() =>
        {
            var consumerConfig = new ConsumerConfig
            {
                BootstrapServers = _config["Kafka:BootstrapServers"],
                GroupId = _config["Kafka:GroupId"],
                AutoOffsetReset = AutoOffsetReset.Earliest
            };

            using var consumer = new ConsumerBuilder<Null, string>(consumerConfig).Build();
            consumer.Subscribe(_config["Kafka:InTopic"]);

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var result = consumer.Consume(stoppingToken);
                    var message = result.Message.Value;
                    
                    // Парсим JSON: { "Operation": "...", "Post": { ... } }
                    var jsonDoc = JsonDocument.Parse(message);
                    var operation = jsonDoc.RootElement.GetProperty("Operation").GetString();
                    var postElement = jsonDoc.RootElement.GetProperty("Post");
                    var post = JsonSerializer.Deserialize<Post>(postElement.GetRawText(), new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                    if (post != null)
                    {
                        if (operation == "CREATE" || operation == "UPDATE")
                            _db.CreateOrUpdatePost(post);
                        else if (operation == "DELETE")
                            _db.DeletePost(post.Id);
                        else if (operation == "GET")
                            post = _db.GetPost(post.Id);

                        // Отправляем ответ обратно в Publisher
                        var responseMessage = new Message<Null, string> { Value = JsonSerializer.Serialize(post ?? new Post()) };
                        _producer.Produce(_config["Kafka:OutTopic"], responseMessage);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Kafka Error: {ex.Message}");
                }
            }
        }, stoppingToken);
    }
}