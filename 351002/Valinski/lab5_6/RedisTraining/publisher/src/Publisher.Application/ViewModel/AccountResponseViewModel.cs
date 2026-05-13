namespace Publisher.Application.ViewModel;

public class AccountResponseViewModel
{
    public long Id { get; set; }
    public string Login { get; set; } = string.Empty;
    public string? Firstname { get; set; }
    public string? Lastname { get; set; }
    public string? Role { get; set; }
}
