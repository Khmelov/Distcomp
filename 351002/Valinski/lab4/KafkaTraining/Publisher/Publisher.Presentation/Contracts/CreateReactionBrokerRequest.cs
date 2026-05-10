namespace Publisher.Presentation.Contracts;

public class CreateReactionBrokerRequest
{
    public long Id { get; set; }
    public MsgStatuses Status = MsgStatuses.PENDING;
    public string Country { get; set; } = "BY";
    public long TopicId { get; set; }
    public string Content { get; set; } = string.Empty;

    public CreateReactionBrokerRequest()
    { }

    public CreateReactionBrokerRequest(CreateReactionRequest request)
    {
        Country = request.Country;
        TopicId = request.TopicId;
        Content = request.Content;
    }
}
