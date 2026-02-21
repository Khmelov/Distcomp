namespace Distcomp.Domain.Models
{
    public class Issue
    {
        public long Id { get; set; }
        public long UserId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }

        public List<long> MarkerIds { get; set; } = new();
    }
}