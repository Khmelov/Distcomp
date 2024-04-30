using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class TweetUpdateDTO
    {
        [Required]
        public int id { get; set; }
        public int? userId { get; set; } = null;
        public string title { get; set; } = null;
        public string content { get; set; } = null;
    }
}
