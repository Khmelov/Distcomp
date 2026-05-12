namespace Discussion.src.NewsPortal.Discussion.Domain.Entities
{
    public class KafkaNoteMessage
    {
        public string Action { get; set; } = string.Empty;
        public Note Data { get; set; } = new();
    }
}
