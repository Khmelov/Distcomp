using System.Text.Json;
using System.Text.Json.Serialization;

namespace Shared.Messages;

public class ReactionMessage
{
    [JsonPropertyName("actionType")]
    public ReactionMessages ActionType { get; set; }

    [JsonPropertyName("entity")]
    public JsonElement? Entity { get; set; }

    [JsonPropertyName("correlationId")]
    public Guid? CorrelationId { get; set; }
}
