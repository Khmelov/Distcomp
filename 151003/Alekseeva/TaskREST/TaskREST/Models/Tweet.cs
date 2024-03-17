using System.ComponentModel.DataAnnotations;

namespace TaskREST.Models;

public class Tweet
{
    public long Id { get; set; }
    public long CreatorId { get; set; }
    [MaxLength(32)] public string Title { get; set; } = string.Empty;
    [MaxLength(2048)] public string Content { get; set; } = string.Empty;
    public DateTime Created { get; init; }
    public DateTime Modified { get; set; }
}