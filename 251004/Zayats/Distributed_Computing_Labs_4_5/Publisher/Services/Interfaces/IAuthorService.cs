using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;

namespace Publisher.Services.Interfaces;

public interface IAuthorService
{
    Task<IEnumerable<AuthorResponseDTO>> GetUsersAsync();

    Task<AuthorResponseDTO> GetUserByIdAsync(long id);

    Task<AuthorResponseDTO> CreateUserAsync(AuthorRequestDTO author);

    Task<AuthorResponseDTO> UpdateUserAsync(AuthorRequestDTO author);

    Task DeleteUserAsync(long id);
}