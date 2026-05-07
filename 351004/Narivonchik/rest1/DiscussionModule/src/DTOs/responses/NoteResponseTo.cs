namespace DiscussionModule.DTOs.responses;

public class NoteResponseTo(
        long id,
        long newsId,
        string content, string state)
{
    public long Id { get; set; } = id;

    public long NewsId { get; set; } = newsId;

    public string Content {  get; set; } = content;
}