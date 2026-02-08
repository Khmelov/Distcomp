using System.ComponentModel.DataAnnotations;

namespace Domain.Models;

public class NoteRequestTo : IIdNullEntity
{
    public long? id { get; set; }
    public long authorId {get; set; }
    
    [StringLength(64, MinimumLength = 2)]
    public string title { get; set; }
    
    [StringLength(2048, MinimumLength = 2)]
    public string content { get; set; }
}