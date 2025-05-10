using System;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace WebApplication1.DTO
{
    public enum ReactionState
    {
        PENDING,
        APPROVE,  
        DECLINE    
    }
    public class ReactionRequestTo
    {
        public long? Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = string.Empty;
    }

    public class ReactionResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = string.Empty;
        [JsonConverter(typeof(JsonStringEnumConverter))]
        public ReactionState State { get; set; } = ReactionState.PENDING;
    }

}
