using Application.Dtos;
using Application.Interfaces;
using Domain.Models;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Repositories;

public class UserRepository : IUserRepository
{
    private readonly BlogDbContext _context;

    public UserRepository(BlogDbContext context)
    {
        _context = context;
    }

    public async Task<List<User>> GetAllUsersAsync()
    {
        return await _context.Users.ToListAsync();
    }

    public async Task<User?> GetUserByIdAsync(long id)
    {
        return await _context
            .Users
            .AsNoTracking()
            .FirstOrDefaultAsync(u => u.Id == id);
    }

    public async Task<User> CreateUserAsync(UserCreateDto userDto)
    {
        var user = new User()
        {
            Login = userDto.Login,
            Password = userDto.Password,
            Firstname = userDto.Firstname,
            Lastname = userDto.Lastname,
        };

        await _context
            .Users
            .AddAsync(user);
        await _context.SaveChangesAsync();

        return user;
    }

    public async Task DeleteUsersAsync(long userId)
    {
        var user = await _context
            .Users
            .FindAsync(userId);

        _context
            .Users
            .Remove(user!);

        await _context.SaveChangesAsync();
    }

    public async Task<User> UpdateUserAsync(UserUpdateDto userDto)
    {
        var user = await _context
            .Users
            .FindAsync(userDto.Id);

        user!.Login = userDto.Login;
        user.Password = userDto.Password;
        user.Firstname = userDto.Firstname;
        user.Lastname = userDto.Lastname;

        await _context.SaveChangesAsync();

        return user;
    }
}
