namespace Distcomp.Domain.Models
{
    public class Note
    {
        public long Id { get; set; }
        public long IssueId { get; set; }
        public string Content { get; set; } = string.Empty;

        public virtual Issue Issue { get; set; } = null!;
    }
}