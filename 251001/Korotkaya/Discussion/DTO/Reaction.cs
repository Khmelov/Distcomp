using System;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Discussion.DTO
{
    public enum ReactionState
    {
        PENDING,  
        APPROVE, 
        DECLINE  
    }
    public class ReactionRequestTo
    {
        [JsonPropertyName("id")]
        public long? Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
    public class ReactionResponseTo
    {
        public long Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = string.Empty;
        [JsonConverter(typeof(JsonStringEnumConverter))]
        public ReactionState State { get; set; } = ReactionState.PENDING;
    }
    public class NumericStringConverter : JsonConverter<string>
    {
        public override string Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            if (reader.TokenType == JsonTokenType.Number)
            {
                long value = reader.GetInt64();
                return value.ToString();
            }
            else if (reader.TokenType == JsonTokenType.String)
            {
                return reader.GetString()!;
            }
            throw new JsonException("Unexpected token type");
        }

        public override void Write(Utf8JsonWriter writer, string value, JsonSerializerOptions options)
        {
            if (long.TryParse(value, out long numericValue))
            {
                writer.WriteNumberValue(numericValue);
            }
            else
            {
                writer.WriteStringValue(value);
            }
        }
    }
}
