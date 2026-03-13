using DistComp.Domain.Abstractions;

namespace DistComp.Domain.Entities;

public class Comment : BaseEntity {
    public long IssueId { get; set; }
    public string Content { get; set; } = "";
}