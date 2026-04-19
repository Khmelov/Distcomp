using System.ComponentModel.DataAnnotations;

namespace DiscussionModule.DTOs.requests;

public class MarkRequestTo
{
    public long? Id { get; set; }

    [StringLength(32, MinimumLength = 2)]
    public string? Name { get; set; }
}