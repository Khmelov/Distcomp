namespace rest1.application.configs;

public class JwtSettings
{
    public const string SectionName = "Jwt";
    
    public string Secret { get; set; } = string.Empty;      // Секретный ключ
    public string Issuer { get; set; } = string.Empty;      // Издатель токена
    public string Audience { get; set; } = string.Empty;    // Получатель токена
    public int ExpirationMinutes { get; set; } = 60;
}