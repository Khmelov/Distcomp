namespace Core.Entities
{
    public class News : Entity
    {
        public long EditorId { get; set; }

        public string Title { get; set; } = string.Empty;

        public string Content { get; set; } = string.Empty;

        public DateTime CreatedAt { get; set; }

        public DateTime Modified { get; set; }

        // navigation

        public Editor? Editor { get; set; }
    }
}
