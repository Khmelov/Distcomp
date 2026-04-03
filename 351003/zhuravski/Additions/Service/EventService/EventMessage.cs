namespace Additions.Service.EventService;

public class EventMessage<T>
{
    public Guid MessageId {get;} = Guid.NewGuid();
    public required string Operation {get; init;} = null!;
    public T Payload {get; init;} = default!;
    public string? Error {get; init;} = null;
    public DateTime Timestamp = DateTime.Now;
}