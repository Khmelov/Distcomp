using System.Text.Json;

namespace Additions.Messaging.Interfaces;

public class EventMessage
{
    public Guid MessageId {get; init;} = Guid.NewGuid();
    public required string Operation {get; init;} = null!;
    public string? Error {get; init;} = null;
    public Guid? InReplyTo {get; init;}
    public DateTime Timestamp {get; init;} = DateTime.Now;
    public string Payload {get; init;} = default!;

    public T? GetPayload<T>()
    {
        return (Payload != null) ? JsonSerializer.Deserialize<T>(Payload) : default;
    }
}