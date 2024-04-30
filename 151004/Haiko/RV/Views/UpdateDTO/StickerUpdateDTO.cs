using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class StickerUpdateDTO
    {
        [Required]
        public int id { get; set; }
        public string name { get; set; } = null;
    }
}
