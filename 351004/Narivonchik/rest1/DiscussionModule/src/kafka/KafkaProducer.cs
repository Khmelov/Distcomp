using Confluent.Kafka;
using System.Text.Json;
using Confluent.Kafka.Admin;

namespace DiscussionModule.kafka;

public class KafkaProducer : IDisposable
{
    private readonly IProducer<string, string> _producer;
    private readonly string _outTopic;
    private readonly string _bootstrapServers;

    public KafkaProducer(string bootstrapServers, string outTopic)
    {
        _bootstrapServers = bootstrapServers;
        var config = new ProducerConfig
        {
            BootstrapServers = bootstrapServers,
            ClientId = "discussion-module-producer",
            Acks = Acks.All,
            EnableIdempotence = true
        };

        _producer = new ProducerBuilder<string, string>(config).Build();
        _outTopic = outTopic;
    }

    public async Task SendMessageAsync(string key, object message)
    {
        try
        {
            var jsonMessage = JsonSerializer.Serialize(message);
            var result = await _producer.ProduceAsync(_outTopic, new Message<string, string>
            {
                Key = key,
                Value = jsonMessage
            });

        }
        catch (ProduceException<string, string> ex)
        {
            Console.WriteLine($"[Kafka] Error: {ex.Error.Reason}");
            throw;
        }
    }

    public async Task EnsureTopicsExistAsync(params string[] topics)
    {
        try
        {
            var config = new AdminClientConfig
            {
                BootstrapServers = _bootstrapServers
            };

            using var adminClient = new AdminClientBuilder(config).Build();
            
            var topicSpecifications = topics.Select(topic => new TopicSpecification
            {
                Name = topic,
                NumPartitions = 3,
                ReplicationFactor = 1
            }).ToList();

            try
            {
                await adminClient.CreateTopicsAsync(topicSpecifications);
                Console.WriteLine($"[Kafka] Topics created: {string.Join(", ", topics)}");
            }
            catch (CreateTopicsException ex)
            {
                if (!ex.Results.Any(r => r.Error.Code != ErrorCode.TopicAlreadyExists))
                {
                    Console.WriteLine("[Kafka] Topics already exist");
                }
                else
                {
                    Console.WriteLine($"[Kafka] Error: {ex.Message}");
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[Kafka] Warning: {ex.Message}");
        }
    }

    public void Dispose()
    {
        _producer?.Dispose();
    }
}