namespace Additions.Messaging.Interfaces;

public interface IEventOrchestrator
{
    Task<EventMessage> ExpectResponse(Guid origin);
    void ResolveResponse(EventMessage message);
}