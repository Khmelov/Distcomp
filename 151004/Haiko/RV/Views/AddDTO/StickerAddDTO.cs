using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class StickerAddDTO
    {
        [Required]
        public string name { get; set; }
    }
}
