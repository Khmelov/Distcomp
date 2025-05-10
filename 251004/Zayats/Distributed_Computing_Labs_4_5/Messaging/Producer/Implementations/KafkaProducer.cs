using Confluent.Kafka;
using Messaging.Producer.Interfaces;
using Microsoft.Extensions.Options;

namespace Messaging.Producer.Implementations;

public class KafkaProducer<TK, TV> : IKafkaProducer<TK, TV>, IDisposable
{
    private readonly IProducer<TK, TV> _producer;
    private readonly KafkaProducerConfig _config;

    public KafkaProducer(IOptions<KafkaProducerConfig> topicOptions,
        IProducer<TK, TV> producer)
    {
        _config = topicOptions.Value;
        _producer = producer;
    }
    
    public async Task ProduceAsync(TK key, TV value)
    {
        await _producer.ProduceAsync(_config.Topic, 
            new Message<TK, TV> { Key = key, Value = value });
    }

    public void Dispose()
    {
        _producer.Flush(TimeSpan.FromSeconds(3));
        _producer.Dispose();
    }
}