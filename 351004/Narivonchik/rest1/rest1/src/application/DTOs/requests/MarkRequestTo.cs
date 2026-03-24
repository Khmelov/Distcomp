using System.ComponentModel.DataAnnotations;

namespace rest1.application.DTOs.requests;

public class MarkRequestTo
{
    public long? Id { get; set; }

    [StringLength(32, MinimumLength = 2)]
    public string? Name { get; set; }
    
    public long[] NewsIds { get; set; }
}