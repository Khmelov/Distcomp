namespace Presentation.Contracts.Requests;

public record TopicRequestTo(
    long UserId,
    string Title,
    string Content,
    string[]? Labels = null);
