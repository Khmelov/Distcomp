namespace Messaging.Consumer.Interfaces;

public interface IKafkaHandler<TK, TV>
{
    Task HandleAsync(TK key, TV value);
}