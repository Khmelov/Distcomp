using System.ComponentModel.DataAnnotations;

namespace Domain.Models;

public class EditorRequestTo: IIdNullEntity
{
    public long? id { get; set; }
    
    [StringLength(64, MinimumLength = 2)]
    public string login { get; set; }
    
    [StringLength(128, MinimumLength = 8)]
    public string password { get; set; }
    
    [StringLength(64, MinimumLength = 2)]
    public string firstname { get; set; }
    
    [StringLength(64, MinimumLength = 2)]
    public string lastname { get; set; }
}