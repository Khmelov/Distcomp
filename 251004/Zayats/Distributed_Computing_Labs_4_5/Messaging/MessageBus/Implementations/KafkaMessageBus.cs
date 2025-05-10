using Messaging.MessageBus.Interfaces;
using Messaging.Producer.Interfaces;

namespace Messaging.MessageBus.Implementations;

public class KafkaMessageBus<TK, TV> : IMessageBus<TK, TV>
{
    public IKafkaProducer<TK, TV> Producer { get; }
    public KafkaMessageBus(IKafkaProducer<TK, TV> producer)
    {
        Producer = producer;
    }
    
    public async Task PublishAsync(TK key, TV message)
    {
        await Producer.ProduceAsync(key, message);
    }
}