using System.ComponentModel.DataAnnotations;

namespace RV_Kisel_lab2_Task320.Models.Dtos;

public class LabelDto {
    public int Id { get; set; }

    [Required]
    [StringLength(32, MinimumLength = 2)]
    public string Name { get; set; } = string.Empty;
}