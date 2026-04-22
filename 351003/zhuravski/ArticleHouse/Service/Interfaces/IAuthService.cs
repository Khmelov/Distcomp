using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface IAuthService
{
    Task<AuthResponseDTO> LoginAsync(LoginRequestDTO dto);
    Task<CreatorResponseDTO> RegisterAsync(CreatorRegistrationDTO dto);
    Task<CreatorResponseDTO> GetCurrentCreatorAsync(string login);
}