namespace RW.Api.Security;

public class JwtSettings
{
    public string Issuer { get; set; } = "RW.Api";
    public string Audience { get; set; } = "RW.Api";
    public string SecretKey { get; set; } = string.Empty;
    public int ExpirationMinutes { get; set; } = 60;
}
