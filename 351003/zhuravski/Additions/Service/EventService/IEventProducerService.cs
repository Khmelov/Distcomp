namespace Additions.Service.EventService;

public interface IEventProducerService
{
    Task ProduceEventAsync<T>(string topic, string key, EventMessage<T> message);
}