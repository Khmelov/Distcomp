namespace DiscussionModule.DTOs.responses;

public class MarkResponseTo(
        long id, 
        string name)
{
    public long? Id { get; set; } = id;

    public string? Name { get; set; } = name;
}
