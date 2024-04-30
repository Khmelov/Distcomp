using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using RV.Models;

namespace RV.Models
{
    [Table("tbl_Note")]
    public class Note
    {
        [Key, DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Required]
        public int id { get; set; }

        [ForeignKey("Tweet")]
        [Required]
        public int tweetId { get; set; }

        [MaxLength(2048)]
        public string content { get; set; }

        public Tweet Tweet { get; set; }
    }
}
