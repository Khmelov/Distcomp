namespace Publisher.Models
{
    public enum CommentState
    {
        PENDING,
        APPROVE,
        DECLINE
    }

    public class Comment
    {
        public long Id { get; set; }
        public long StoryId { get; set; }
        public string Content { get; set; } = string.Empty;
        public string Country { get; set; } = string.Empty;
    
        public CommentState State { get; set; } = CommentState.PENDING;
    }
}