using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class UserUpdateDTO
    {
        public int? id { get; set; }
        public string? login { get; set; } = null;
        public string? password { get; set; } = null;
        public string? firstname { get; set; } = null;
        public string? lastname { get; set; } = null;
        public DateTime? modified { get; set; } = null;
    }
}
