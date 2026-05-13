using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Entities;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Services;

public class AuthService
{
    private readonly AppbContext _context;
    private readonly JwtTokenService _jwtTokenService;

    public AuthService(AppbContext context, JwtTokenService jwtTokenService)
    {
        _context = context;
        _jwtTokenService = jwtTokenService;
    }

    public async Task<TblAuthor> RegisterAsync(AuthorRequestDto dto)
    {
        var login = dto.Login?.Trim();
        if (string.IsNullOrWhiteSpace(login) || string.IsNullOrWhiteSpace(dto.Password))
        {
            throw new InvalidOperationException("Login and password are required.");
        }

        var exists = await _context.TblAuthors.AnyAsync(x => x.Login == login);
        if (exists)
        {
            throw new InvalidOperationException("Author with this login already exists.");
        }

        var nextId = _context.TblAuthors.Any() ? _context.TblAuthors.Max(x => x.Id) + 1 : 1;
        var author = new TblAuthor
        {
            Id = nextId,
            Login = login,
            Password = BCrypt.Net.BCrypt.HashPassword(dto.Password),
            Firstname = dto.Firstname ?? string.Empty,
            Lastname = dto.Lastname ?? string.Empty,
            Role = NormalizeRole(dto.Role)
        };

        _context.TblAuthors.Add(author);
        await _context.SaveChangesAsync();
        return author;
    }

    public async Task<string> LoginAsync(LoginRequestDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Login) || string.IsNullOrWhiteSpace(dto.Password))
        {
            throw new UnauthorizedAccessException("Invalid login or password.");
        }

        var author = await _context.TblAuthors.FirstOrDefaultAsync(x => x.Login == dto.Login);
        if (author == null || !BCrypt.Net.BCrypt.Verify(dto.Password, author.Password))
        {
            throw new UnauthorizedAccessException("Invalid login or password.");
        }

        return _jwtTokenService.GenerateToken(author);
    }

    private static string NormalizeRole(string? role)
    {
        if (string.Equals(role, "ADMIN", StringComparison.OrdinalIgnoreCase))
        {
            return "ADMIN";
        }

        return "CUSTOMER";
    }
}
