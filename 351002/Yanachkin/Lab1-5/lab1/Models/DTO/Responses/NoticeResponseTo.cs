namespace lab1.Models.DTO.Responses;

public class NoticeResponseTo
{
    public long Id { get; set; }
    public long IssueId { get; set; }
    public string Content { get; set; } = null!;
    public string State { get; set; } = "APPROVE";
}
