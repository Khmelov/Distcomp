using System.ComponentModel.DataAnnotations;

namespace RV_Kisel_lab2_Task320.Models.Dtos;

public class PostDto {
    public int Id { get; set; }

    [Required]
    [StringLength(2048, MinimumLength = 2)]
    public string Content { get; set; } = string.Empty;

    public int NewsId { get; set; }
}