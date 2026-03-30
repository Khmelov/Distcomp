namespace Project.Model {
    public class Reaction : BaseEntity {
        public long TweetId { get; set; }
        public Tweet? Tweet { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}