namespace Application.DTO.Request.Editor;

public class EditorRequestToCreate
{
    public string Login { get; init; }
    public string Password { get; init; }
    public string Firstname { get; init; }
    public string? Lastname { get; init; }
}