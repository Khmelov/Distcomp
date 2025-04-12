namespace Application.DTO.Response;

public record NoticeResponseTo
{
    public long Id { get; init; }
    public long NewsId { get; init; }
    public string Content { get; init; }

    public NoticeResponseTo() { }
}