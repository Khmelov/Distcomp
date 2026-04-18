using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface IAuthService
{
    Task<AuthResponseDTO> LoginAsync(string login, string password);
    Task<CreatorResponseDTO> RegisterAsync(CreatorRegistrationDTO dto);
    Task<CreatorResponseDTO> GetCurrentCreatorAsync(string login);
}