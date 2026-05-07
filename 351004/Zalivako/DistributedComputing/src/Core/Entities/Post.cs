namespace Core.Entities
{
    public enum PostState
    {
        PENDING,
        APPROVE,
        DECLINE
    }

    public class Post : Entity
    {

        public long NewsId { get; set; }

        public string Content { get; set; } = string.Empty;

        public string Country { get; set; } = string.Empty;

        public PostState State { get; set; } = PostState.PENDING;

        // navigation

        public News? News { get; set; }
    }
}
