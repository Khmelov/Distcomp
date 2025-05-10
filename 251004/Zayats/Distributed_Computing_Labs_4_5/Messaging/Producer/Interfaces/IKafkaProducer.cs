namespace Messaging.Producer.Interfaces;

public interface IKafkaProducer<TK, TV>
{
    Task ProduceAsync(TK key, TV value);
}