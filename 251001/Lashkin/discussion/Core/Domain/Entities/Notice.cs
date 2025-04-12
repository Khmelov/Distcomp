using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Domain.Entities;

public class Notice
{
    [BsonId]
    [BsonRepresentation(BsonType.Int64)]
    [JsonPropertyName("id")]
    public long Id { get; set; }
    public long NewsId { get; set; }
    public string Country { get; set; } = null!;
    public string Content { get; set; } = null!; 
}