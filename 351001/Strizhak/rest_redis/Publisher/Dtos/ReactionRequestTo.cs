namespace Publisher.Dtos
{
    public class ReactionRequestTo
    {
        public long Id { get; set; }          
        public long TopicId { get; set; }
        public string Content { get; set; } = null!;
        public string? State { get; set; }

    }
}
