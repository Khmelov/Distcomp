using Discussion.Domain.Abstractions;

namespace Discussion.Domain.Entities;

public class Comment : BaseEntity
{
    public long IssueId { get; set; }
    public string Country { get; set; } = null!;
    public string Content { get; set; } = null!;

    public string? AuthorLogin { get; set; }

    public string State { get; set; } = "PENDING";
}