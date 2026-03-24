namespace rest1.application.DTOs.responses;

public class NoteResponseTo(
        long id,
        long newsId,
        string content)
{
    public long Id { get; set; } = id;

    public long NewsId { get; set; } = newsId;

    public string Content {  get; set; } = content;
}