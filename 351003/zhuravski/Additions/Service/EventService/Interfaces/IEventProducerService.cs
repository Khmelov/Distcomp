namespace Additions.Service.EventService.Interfaces;

public interface IEventProducerService
{
    Task ProduceEventAsync(string topic, EventMessage message);
    Task<EventMessage> ProduceEventWithResponseAsync(string topic, EventMessage message);
}