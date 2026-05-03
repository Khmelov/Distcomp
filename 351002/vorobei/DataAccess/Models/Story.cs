using System.ComponentModel.DataAnnotations.Schema;

namespace DataAccess.Models
{
    public class Story : BaseEntity
    {
        [Column("creator_id")]
        public int CreatorId { get; set; }

        [Column("title")]
        public string Title { get; set; } = string.Empty;

        [Column("content")]
        public string Content { get; set; } = string.Empty;

        [Column("created")]
        public DateTime Created { get; set; }

        [Column("modified")]
        public DateTime Modified { get; set; }

        public Creator Creator { get; set; }
        public ICollection<Post> Posts { get; set; }
        public ICollection<Mark> Marks { get; set; }
    }
}
