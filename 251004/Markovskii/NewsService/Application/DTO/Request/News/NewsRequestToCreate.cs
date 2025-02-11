namespace Application.DTO.Request.News;

public class NewsRequestToCreate
{
    public long EditorId { get; init; }
    public string Title { get; init; }
    public string Content { get; init; }
}