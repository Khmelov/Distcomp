namespace publisherApi.Models.DTOs
{
    // Role is nullable and defaults to null
    public record RegisterRequestTo(string Login, string Password, string FirstName, string LastName, string? Role = null);
}