using System.ComponentModel.DataAnnotations;

namespace ServerApp.Models.DTOs.Requests;

public record StickerRequestTo(
    [Required]
    [StringLength(32, MinimumLength = 2, ErrorMessage = "Название стикера должно быть от 2 до 32 символов")]
    string Name
);