    public class CommentResponseDto
    {
        public long Id { get; set; }
        public long StoryId { get; set; }
        public string Content { get; set; } = string.Empty;
        public string Country { get; set; } = string.Empty;
        public string State { get; set; } = "PENDING";
    }