namespace Core.Entities
{
    public class Post : Entity
    {

        public long NewsId { get; set; }

        public string Content { get; set; } = string.Empty;

        // navigation

        public News? News { get; set; }
    }
}
