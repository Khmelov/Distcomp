using System.ComponentModel.DataAnnotations;

namespace Discussion.Views.DTO
{
    public class NoteAddDTO
    {
        [Required]
        public int id { get; set; }
        [Required]
        public int tweetId { get; set; }
        [Required]
        public string content { get; set; }
    }
}
