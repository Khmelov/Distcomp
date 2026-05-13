namespace discussion.Models.DTO.Requests;

public class NoticeRequestTo
{
    public long Id { get; set; }
    public long IssueId { get; set; }
    public string Content { get; set; } = null!;
}
