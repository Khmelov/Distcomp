namespace discussion.Models.DTO.Responses;

public class NoticeResponseTo
{
    public long Id { get; set; }
    public long IssueId { get; set; }
    public string Content { get; set; } = null!;
    /// <summary>PENDING при асинхронном черновике на publisher или итог APPROVE/DECLINE после модерации.</summary>
    public string State { get; set; } = "APPROVE";
}
