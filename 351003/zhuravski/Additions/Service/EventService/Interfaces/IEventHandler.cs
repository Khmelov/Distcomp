namespace Additions.Service.EventService.Interfaces;

public interface IEventHandler
{
    string SupportedOperation {get;}
    Task HandleMessage(EventMessage message);
}