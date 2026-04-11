namespace Additions.Service.EventService.Interfaces;

public interface IEventOrchestratorService
{
    Task<EventMessage> ExpectResponse(Guid origin);
    void ResolveResponse(EventMessage message);
}