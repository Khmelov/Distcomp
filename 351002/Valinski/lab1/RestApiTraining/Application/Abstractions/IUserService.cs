using Application.Dtos;

namespace Application.Abstractions;

public interface IUserService
{
    Task<UserGetDto?> CreateUserAsync(UserCreateDto createDto);
    Task<List<UserGetDto>> GetAllUsers();
    Task<UserGetDto> GetUserById(long id);
    Task<UserGetDto> UpdateUserAsync(UserUpdateDto updateDto);
    Task<bool> DeleteUserAsync(long id);
}
