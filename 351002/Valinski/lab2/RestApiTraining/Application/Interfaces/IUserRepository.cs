using Application.Dtos;
using Domain.Models;

namespace Application.Interfaces;

public interface IUserRepository
{
    Task<List<User>> GetAllUsersAsync();
    Task<User?> GetUserByIdAsync(long id);
    Task<User?> GetUserByLoginAsync(string login);
    Task<User> CreateUserAsync(UserCreateDto userDto);
    Task DeleteUsersAsync(long userId);
    Task<User> UpdateUserAsync(UserUpdateDto userDto);
}
