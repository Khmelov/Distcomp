namespace Distcomp.Discussion.Domain.Models
{
    public class Note
    {
        public string Country { get; set; } = "BY";

        public long IssueId { get; set; }
        public long Id { get; set; }

        public string Content { get; set; } = string.Empty;
    }
}