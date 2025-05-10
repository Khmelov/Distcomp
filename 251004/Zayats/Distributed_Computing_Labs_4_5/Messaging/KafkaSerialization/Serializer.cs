using System.Text;
using System.Text.Json;
using Confluent.Kafka;

namespace Messaging.KafkaSerialization;

public class Serializer<T> : ISerializer<T>
{
    public byte[] Serialize(T data, SerializationContext context)
    {
        if (typeof(T) == typeof(Null))
            return null!;

        if (typeof(T) == typeof(Ignore))
            throw new NotSupportedException("Not Supported.");

        var json = JsonSerializer.Serialize<T>(data);

        return Encoding.UTF8.GetBytes(json);
    }
}