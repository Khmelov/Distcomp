namespace Messaging.MessageBus.Interfaces;

public interface IMessageBus<TK, TV>
{
    Task PublishAsync(TK key, TV message);
}