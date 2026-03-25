using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace DiscussionService.Models
{
    public class Post
    {
        [BsonId] // основной ключ
        [BsonRepresentation(BsonType.Int64)]
        public long Id { get; set; }

        [BsonElement("newsId")]
        public long NewsId { get; set; }

        [BsonElement("content")]
        public string Content { get; set; } = string.Empty;

        [BsonElement("country")]
        public string Country { get; set; } = string.Empty;
    }
}
