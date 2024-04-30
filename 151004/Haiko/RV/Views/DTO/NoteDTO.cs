using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class NoteDTO
    {
        [Required]
        public int id { get; set; }
        [Required]
        public int tweetId { get; set; }
        [Required]
        public string content { get; set; }
        public string country { get; set; }
    }
}
