namespace ArticleHouse.Service.DTOs;

public class CreatorRegistrationDTO
{
    public required string Login { get; init; }
    public required string Password { get; init; }
    public required string FirstName { get; init; }
    public required string LastName { get; init; }
    public required string Role { get; init; }
}