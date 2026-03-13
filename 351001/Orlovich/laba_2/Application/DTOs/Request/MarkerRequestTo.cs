using System.ComponentModel.DataAnnotations;

namespace Domain.Models;

public class MarkerRequestTo: IIdNullEntity
{
    public long? id { get; set; }
    
    [StringLength(32, MinimumLength = 2)]
    public string name { get; set; }
}