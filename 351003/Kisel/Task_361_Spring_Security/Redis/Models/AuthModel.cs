namespace Redis.Models;

public class LoginRequest
{
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
}

public class LoginResponse
{
    public string access_token { get; set; } = string.Empty;
}

public class ErrorResponse
{
    public string errorMessage { get; set; } = string.Empty;
    public string errorCode { get; set; } = string.Empty;
}