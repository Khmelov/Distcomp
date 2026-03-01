namespace Distcomp.Domain.Models
{
    public class Issue
    {
        public long Id { get; set; }
        public long UserId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }

        public virtual User User { get; set; } = null!;
        public virtual ICollection<Marker> Markers { get; set; } = new List<Marker>();
        public virtual ICollection<Note> Notes { get; set; } = new List<Note>();
    }
}