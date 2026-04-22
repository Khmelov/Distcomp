using System.ComponentModel.DataAnnotations.Schema;

namespace DataAccess.Models
{
    public class Post : BaseEntity
    {
        [Column("story_id")]
        public int StoryId { get; set; }

        [Column("contex")]
        public string Content { get; set; } = string.Empty;

        public Story Story { get; set; }
    }
}
