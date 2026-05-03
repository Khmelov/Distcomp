using System.ComponentModel.DataAnnotations;

namespace DiscussionModule.DTOs.requests;

public class NoteRequestTo
{
    public long? Id { get; set; }

    public long NewsId { get; set; }

    [StringLength(2048, MinimumLength = 2)]
    public string Content { get; set; }
    // public string Country { get; set; }
    // public string? State { get; set; } // PENDING, APPROVE, DECLINE
}