using System.ComponentModel.DataAnnotations.Schema;
using Discussion.Domain.Abstractions;
using Shared.Enums;

namespace Discussion.Domain.Entities;

public class Comment : BaseEntity 
{
    public long IssueId { get; set; }
    public string Country { get; set; } = null!;
    public string Content { get; set; } = null!;
    
    public string State { get; set; } = "PENDING"; 
}