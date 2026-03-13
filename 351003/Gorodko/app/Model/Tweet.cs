namespace Project.Model {
    public class Tweet : BaseEntity {
        public long EditorId { get; set; }
        public Editor? Editor { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }

        public List<Reaction> Reactions { get; set; } = new();
        public List<Sticker> Stickers { get; set; } = new();
    }
}