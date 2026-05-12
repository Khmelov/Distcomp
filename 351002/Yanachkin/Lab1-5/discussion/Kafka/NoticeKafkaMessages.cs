using discussion.Models.DTO.Responses;

namespace discussion.Kafka;

public sealed class NoticeInEnvelope
{
    public string Kind { get; set; } = "";
    public string? CorrelationId { get; set; }
    public long Id { get; set; }
    public long IssueId { get; set; }
    public long NoticeId { get; set; }
    public string? Content { get; set; }
    public int Page { get; set; }
    public int Size { get; set; }
    public string? Sort { get; set; }
}

public sealed class NoticeOutEnvelope
{
    public string CorrelationId { get; set; } = "";
    public bool Ok { get; set; }
    public string? Error { get; set; }
    public NoticeResponseTo? Notice { get; set; }
    public List<NoticeResponseTo>? Notices { get; set; }
    public PageResponseTo<NoticeResponseTo>? Page { get; set; }
}
