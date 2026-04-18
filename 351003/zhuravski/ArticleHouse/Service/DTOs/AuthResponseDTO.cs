namespace ArticleHouse.Service.DTOs;

public class AuthResponseDTO
{
    public required string AccessToken { get; init; } 
    public required string TokenType { get; init; } 
    public int ExpiresIn { get; init; } = 3600; 
}