using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class StickerDTO
    {
        [Required]
        public int id { get; set; }
        [Required]
        public string name { get; set; }
    }
}
