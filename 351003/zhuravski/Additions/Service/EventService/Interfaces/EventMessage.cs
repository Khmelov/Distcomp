using System.Text.Json;

namespace Additions.Service.EventService.Interfaces;

public class EventMessage
{
    public Guid MessageId {get;} = Guid.NewGuid();
    public required string Operation {get; init;} = null!;
    public string? Error {get; init;} = null;
    public Guid? InReplyTo {get; init;}
    public DateTime Timestamp = DateTime.Now;
    public JsonElement Payload {get; init;} = default!;

    public T? GetPayload<T>()
    {
        return Payload.Deserialize<T>();
    }
}