namespace Project.Model {
    public class Sticker : BaseEntity {
        public string Name { get; set; } = string.Empty;
        public List<Tweet> Tweets { get; set; } = new();
    }
}