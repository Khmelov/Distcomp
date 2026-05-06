using System.ComponentModel.DataAnnotations.Schema;

namespace DataAccess.Models
{
    public class Mark : BaseEntity
    {
        [Column("name")]
        public string Name { get; set; } = string.Empty;
        public ICollection<Story> Stories { get; set; }
    }
}
