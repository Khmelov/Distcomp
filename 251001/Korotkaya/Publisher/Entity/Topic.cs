using System.ComponentModel.DataAnnotations.Schema;

namespace WebApplication1.Entity
{
    public class Topic
    {
        public long Id { get; set; }

        [Column("editor_id")]
        public long EditorId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; } = DateTime.UtcNow;
        public DateTime Modified { get; set; } = DateTime.UtcNow;

        public Editor Editor { get; set; } = null!;
        public List<Reaction> Reactions { get; set; } = new();
        public List<Tag> Tags { get; set; } = new();
    }
}
