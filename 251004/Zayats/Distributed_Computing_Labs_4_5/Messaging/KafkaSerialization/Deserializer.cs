using System.Text;
using System.Text.Json;
using Confluent.Kafka;

namespace Messaging.KafkaSerialization;

public class Deserializer<T> : IDeserializer<T>
{
    public T Deserialize(ReadOnlySpan<byte> data, bool isNull, SerializationContext context)
    {
        if (typeof(T) == typeof(Null))
        {
            if (data.Length > 0)
                throw new ArgumentException("The data is null not null.");
            return default;
        }

        if (typeof(T) == typeof(Ignore))
            return default;

        var dataJson = Encoding.UTF8.GetString(data);

        return JsonSerializer.Deserialize<T>(dataJson)!;
    }
}