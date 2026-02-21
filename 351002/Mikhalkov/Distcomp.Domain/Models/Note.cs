namespace Distcomp.Domain.Models
{
    public class Note
    {
        public long Id { get; set; }
        public long IssueId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}