using System.Text.Json.Serialization;

namespace Discussion.DTOs;

public class NoticeResponseDTO
{
    public int Id { get; set; }
    public string Content { get; set; }
    [JsonPropertyName("newsId")]
    public int NewsId { get; set; }
    public NoticeState State { get; set; }
    public OperationType OperationType { get; set; }

}


