namespace Presentation.Contracts;

public class LabelUpdateRequest
{
    public long Id { get; set; }
    public string Name { get; set; } = null!;
}
