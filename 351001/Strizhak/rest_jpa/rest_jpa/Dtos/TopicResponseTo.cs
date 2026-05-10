namespace rest_api.Dtos
{
    public class TopicResponseTo
    {
        public long Id { get; set; }
        public long UserId { get; set; }
        public string Title { get; set; } = null!;
        public string Content { get; set; } = null!;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }
    }
}
