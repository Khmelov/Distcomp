using System.ComponentModel.DataAnnotations;

namespace Domain.Models;

public class StoryRequestTo: IIdNullEntity
{
    public long? id { get; set; }
    public long issueId { get; set; }
    
    [StringLength(2048, MinimumLength = 2)]
    public string content { get; set; }
}