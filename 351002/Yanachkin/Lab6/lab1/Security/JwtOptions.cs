namespace lab1.Security;

public sealed class JwtOptions
{
    public const string SectionName = "Jwt";
    public string Issuer { get; set; } = "lab1";
    public string Audience { get; set; } = "lab1-clients";
    public string Secret { get; set; } = "SUPER_SECRET_KEY_CHANGE_ME_1234567890";
    public int ExpirationSeconds { get; set; } = 60;
}
