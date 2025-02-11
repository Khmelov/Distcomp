namespace Application.DTO.Request.News;

public class NewsRequestToFullUpdate
{
    public long Id { get; init; }
    public long EditorId { get; init; }
    public string Title { get; init; }
    public string Content { get; init; }
}