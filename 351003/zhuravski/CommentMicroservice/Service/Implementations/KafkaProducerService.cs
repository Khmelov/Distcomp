using Additions.Service;
using CommentMicroservice.Service.Interfaces;
using Confluent.Kafka;

namespace CommentMicroservice.Service.Implementations;

public class KafkaProducerService : IEventProducerService, IDisposable
{
    private IProducer<string, string> producer;

    public KafkaProducerService(IConfiguration configuration)
    {
        string bootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092";
        producer = new ProducerBuilder<string, string>(new ProducerConfig()
        {
            BootstrapServers = bootstrapServers,
            AllowAutoCreateTopics = true
        }).Build();
    }

    public async Task ProduceEventAsync(string topic, string key, string value)
    {
        try
        {
            var deliveryResult = await producer.ProduceAsync(topic, new Message<string, string>
            {
                Key = key,
                Value = value
            });
        }
        catch (ProduceException<string, string> e)
        {
            
            throw new ServiceException($"Failed to produce message to {topic}: {e.Error.Reason}");
        }
    }

    public void Dispose()
    {
        if (producer != null)
        {
            GC.SuppressFinalize(this);
            producer.Flush(TimeSpan.FromSeconds(15));
            producer.Dispose();
            producer = null!;
        }
    }
}