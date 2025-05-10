using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.DTO.ResponseDTO;

namespace Distributed_Computing_Lab_1.Services.Interfaces;

public interface IAuthorService
{
    Task<IEnumerable<AuthorResponseDTO>> GetUsersAsync();

    Task<AuthorResponseDTO> GetUserByIdAsync(long id);

    Task<AuthorResponseDTO> CreateUserAsync(AuthorRequestDTO author);

    Task<AuthorResponseDTO> UpdateUserAsync(AuthorRequestDTO author);

    Task DeleteUserAsync(long id);
}