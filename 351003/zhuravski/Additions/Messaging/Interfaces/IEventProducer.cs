namespace Additions.Messaging.Interfaces;

public interface IEventProducer
{
    Task ProduceEventAsync(string topic, EventMessage message);
    Task<EventMessage> ProduceEventWithResponseAsync(string topic, EventMessage message);
}