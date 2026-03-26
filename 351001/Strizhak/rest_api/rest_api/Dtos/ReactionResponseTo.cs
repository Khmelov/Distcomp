namespace rest_api.Dtos
{
    public class ReactionResponseTo
    {
        public long Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = null!;
    }
}
