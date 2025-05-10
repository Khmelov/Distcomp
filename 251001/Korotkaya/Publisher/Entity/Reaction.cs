namespace WebApplication1.Entity
{
    public class Reaction
    {
        public long Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = string.Empty;

        public Topic Topic { get; set; } = null!;
    }
}
