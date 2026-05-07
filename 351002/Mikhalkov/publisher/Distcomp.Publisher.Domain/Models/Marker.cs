namespace Distcomp.Domain.Models
{
    public class Marker
    {
        public long Id { get; set; }
        public string Name { get; set; } = string.Empty;

        public virtual ICollection<Issue> Issues { get; set; } = new List<Issue>();
    }
}