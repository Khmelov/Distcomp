namespace Application.Interfaces
{
    public interface IAuthService
    {
        Task<string?> AuthenticateAsync(string login, string password);
    }
}