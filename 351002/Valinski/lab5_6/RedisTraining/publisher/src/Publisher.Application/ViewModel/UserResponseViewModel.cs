namespace Publisher.Application.ViewModel;

public class UserResponseViewModel
{
    public long Id { get; set; }
    public string Login { get; set; } = string.Empty;
    public string? Firstname { get; set; }
    public string? Lastname { get; set; }
}
