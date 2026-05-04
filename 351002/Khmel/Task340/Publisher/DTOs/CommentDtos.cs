using System.ComponentModel.DataAnnotations;
using Publisher.Models;

namespace Publisher.DTOs
{
    public class KafkaCommentMessage
    {
        public string Action { get; set; } = string.Empty; // CREATE, UPDATE, DELETE
        public CommentData Data { get; set; } = new();
    }

    public class CommentData
    {
        public long Id { get; set; }
        public long StoryId { get; set; }
        public string Content { get; set; } = string.Empty;
        public string Country { get; set; } = string.Empty;
        public string State { get; set; } = "PENDING";
    }
}