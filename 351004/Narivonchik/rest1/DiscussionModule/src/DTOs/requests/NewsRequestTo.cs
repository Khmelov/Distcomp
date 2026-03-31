using System.ComponentModel.DataAnnotations;

namespace DiscussionModule.DTOs.requests;

public class NewsRequestTo
{
    public long? Id { get; set; }

    public long CreatorId { get; set; }

    [StringLength(64, MinimumLength = 2)]
    public string Title { get; set; } = string.Empty;

    [StringLength(2048, MinimumLength = 4)]
    public string Content { get; set; } = string.Empty;
    
    public List<string> Marks { get; set; } = [];
}