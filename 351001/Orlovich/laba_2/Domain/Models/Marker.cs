namespace Domain.Models;

// Marker
public class Marker: IIdEntity
{
    public long id { get; set; } = 0;
    public string name { get; set; }
    public ICollection<Issue> issues { get; set; }
}