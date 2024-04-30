using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class TweetDTO
    {
        [Required]
        public int id { get; set; }
        [Required]
        public int userId { get; set; }
        [Required]
        public string title { get; set; }
        [Required]
        public string content { get; set; }
        [Required]
        public DateTime created { get; set; }
        [Required]
        public DateTime modified { get; set; }
    }
}
