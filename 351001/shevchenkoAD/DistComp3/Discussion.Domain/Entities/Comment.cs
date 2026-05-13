using System.ComponentModel.DataAnnotations.Schema;
using Discussion.Domain.Abstractions;

namespace Discussion.Domain.Entities;

public class Comment : BaseEntity // BaseEntity просто содержит long Id
{
    public long IssueId { get; set; }
    public string Country { get; set; } = null!; // Новое поле по схеме
    public string Content { get; set; } = null!;
}