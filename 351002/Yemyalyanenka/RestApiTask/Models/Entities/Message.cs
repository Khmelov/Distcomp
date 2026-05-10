namespace RestApiTask.Models.Entities
{
    public class Message : IHasId
    {
        public long Id { get; set; }
        public long ArticleId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
