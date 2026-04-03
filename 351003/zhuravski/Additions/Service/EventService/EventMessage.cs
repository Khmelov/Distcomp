namespace Additions.Service.EventService;

public class EventMessage<T>
{
    public Guid MessageId {get;} = Guid.NewGuid();
    public string Operation {get; init;} = null!;
    public T Payload {get; init;} = default!;
    public string? ResponseTopic {get; set;} = null;
    public DateTime Timestamp = DateTime.Now;
}