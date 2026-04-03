namespace Additions.Service.EventService;

public interface IEventProducerService
{
    Task ProduceEventAsync<T>(string topic, EventMessage<T> message);
    Task<EventMessage<X>> ProduceEventWithResponseAsync<T, X>(string topic, EventMessage<T> message, TimeSpan timeout);
}