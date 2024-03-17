using System.ComponentModel.DataAnnotations;

namespace TaskREST.Models;

public class Post
{
    public long Id { get; set; }
    public long TweetId { get; set; }
    [MaxLength(2048)] public string Content { get; set; } = string.Empty;
}