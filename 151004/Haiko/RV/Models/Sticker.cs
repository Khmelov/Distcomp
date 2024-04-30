using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace RV.Models
{
    [Table("tbl_Sticker")]
    public class Sticker
    {
        [Key, DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Required]
        public int id { get; set; }

        [MaxLength(32)]
        [Required]
        public string name { get; set; }

        public List<Tweet> Tweets { get; set; } = new();
        public List<TweetsSticker> TweetsStickers { get; set; } = new();

    }
}
