namespace Publisher.Presentation.Contracts;

public class UserResponse
{
    public long Id { get; set; }

    public string Login { get; set; } = string.Empty;

    public string? Firstname { get; set; }

    public string? Lastname { get; set; }    
}
