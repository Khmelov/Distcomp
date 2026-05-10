using System.ComponentModel.DataAnnotations;

namespace RV_Kisel_lab2_Task320.Models.Dtos;

public class CreatorDto {
    public int Id { get; set; }

    [Required]
    [StringLength(64, MinimumLength = 2)]
    public string Login { get; set; } = string.Empty;

    [Required]
    [StringLength(64, MinimumLength = 8)]
    public string Password { get; set; } = string.Empty;

    [Required]
    [StringLength(64, MinimumLength = 2)]
    public string Firstname { get; set; } = string.Empty;

    [Required]
    [StringLength(64, MinimumLength = 2)]
    public string Lastname { get; set; } = string.Empty;
}