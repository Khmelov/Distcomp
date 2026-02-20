namespace Domain.Models;

// Marker
public class Tag: IIdEntity
{
    public long id { get; set; } = 0;
    public string name { get; set; }
}