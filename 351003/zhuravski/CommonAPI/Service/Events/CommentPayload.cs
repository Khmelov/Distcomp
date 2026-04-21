namespace CommonAPI.Service.Events;

public class CommentPayload
{
    public long Id {get; set;}
    public long ArticleId {get; set;}
    public string Content {get; set;} = default!;
}