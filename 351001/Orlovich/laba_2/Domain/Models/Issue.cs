using System.Xml.Linq;

namespace Domain.Models;

// Issue
public class Issue: IIdEntity
{
    public long id { get; set; }
    public long authorId  { get; set; }
    public string title { get; set; }
    public string content { get; set; }
    public DateTime created { get; set; }
    public DateTime modified { get; set; }
    public Author author { get; set; }
    public ICollection<Comment> comments { get; set; }
    public ICollection<Marker> markers { get; set; }
}