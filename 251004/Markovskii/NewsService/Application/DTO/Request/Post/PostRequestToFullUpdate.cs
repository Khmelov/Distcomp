namespace Application.DTO.Request.Post;

public class PostRequestToFullUpdate
{
    public long Id { get; init; }
    public long NewsId { get; init; }
    public string Content { get; init; }
}