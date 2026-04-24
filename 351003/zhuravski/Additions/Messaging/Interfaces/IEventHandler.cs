namespace Additions.Messaging.Interfaces;

public interface IEventHandler
{
    string SupportedOperation {get;}
    Task HandleMessage(EventMessage message);
}