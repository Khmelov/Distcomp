using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Discussion.Models;

public class Notice
{
    [BsonId]
    [BsonRepresentation(BsonType.Int32)]
    public int? Id { get; set; }
    [BsonElement("Content")]
    public string Content { get; set; }
    [BsonElement("NewsId")]
    public int NewsId { get; set; }
    [BsonElement("CreatedAt")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    [BsonElement("UpdatedAt")]
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
    public NoticeState State { get; set; } = NoticeState.PENDING;
    
}