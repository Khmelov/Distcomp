using publisherApi.Models.DTOs;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;

namespace publisherApi.Services.Interfaces
{
    public interface IAuthService
    {
        Task<LoginResponseTo> LoginAsync(LoginRequestTo request);
        Task<WriterResponseTo> RegisterAsync(RegisterRequestTo request);
        Task<Writer?> GetByLoginAsync(string login);
    }
}
