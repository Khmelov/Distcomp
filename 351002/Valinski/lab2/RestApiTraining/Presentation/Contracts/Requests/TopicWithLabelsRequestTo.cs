namespace Presentation.Contracts.Requests;

public record TopicWithLabelsRequestTo(
    long UserId,
    string Title,
    string Content,
    string[] Labels
    );
