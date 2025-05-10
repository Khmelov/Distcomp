using System.ComponentModel.DataAnnotations;

namespace Discussion.DTOs;

public class NoticeRequestDTO
{
    public int Id { get; set; }
    [StringLength(64, MinimumLength = 3, ErrorMessage = "Длина строки должна быть от 3 до 64 символов")]
    public string Content { get; set; }
    public int NewsId { get; set; } // ID новости, к которой относится уведомление
    public NoticeState State { get; set; } = NoticeState.PENDING;

}