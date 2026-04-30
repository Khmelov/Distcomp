using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace rest1.application.services;

using BCrypt.Net;
using DTOs.requests;
using DTOs.responses;
using exceptions;
using interfaces;
using rest1.application.interfaces.services;

public class AuthService : IAuthService
{
    private readonly ICreatorRepository _creatorRepository;
    private readonly ITokenGenerator _tokenGenerator;
    private readonly ILogger<AuthService> _logger;

    public AuthService(
        ICreatorRepository creatorRepository,
        ITokenGenerator tokenGenerator,
        ILogger<AuthService> logger)
    {
        _creatorRepository = creatorRepository;
        _tokenGenerator = tokenGenerator;
        _logger = logger;
    }

    public async Task<LoginResponseTo> AuthenticateAsync(LoginRequestTo loginRequest)
    {
        var creator = await _creatorRepository.FindByLoginAsync(loginRequest.Login);
        
        if (creator == null)
        {
            throw new UnauthorizedException("Invalid login or password");
        }

        if (!BCrypt.Verify(loginRequest.Password, creator.Password))
        {
            throw new UnauthorizedException("Invalid login or password");
        }

        var token = _tokenGenerator.GenerateToken(creator.Login, creator.Role.ToString());
        
        return new LoginResponseTo
        {
            AccessToken = token,
            TokenType = "Bearer",
            ExpiresIn = 3600 // 1 hour in seconds
        };
    }

    public async Task<UserInfoResponseTo> GetCurrentUserAsync(string login)
    {
        var creator = await _creatorRepository.FindByLoginAsync(login);
        
        if (creator == null)
        {
            throw new NewNotFoundException($"User with login {login} not found");
        }

        return new UserInfoResponseTo
        {
            Id = creator.Id,
            Login = creator.Login,
            Firstname = creator.Firstname,
            Lastname = creator.Lastname,
            Role = creator.Role.ToString()
        };
    }

    public async Task<bool> ValidateTokenAsync(string token)
    {
        return await Task.Run(() => _tokenGenerator.ValidateToken(token));
    }

    public string? GetLoginFromToken(string token)
    {
        var principal = _tokenGenerator.GetPrincipalFromToken(token);
        return principal?.FindFirst(JwtRegisteredClaimNames.Sub)?.Value;
    }

    public string? GetRoleFromToken(string token)
    {
        var principal = _tokenGenerator.GetPrincipalFromToken(token);
        return principal?.FindFirst(ClaimTypes.Role)?.Value 
               ?? principal?.FindFirst("role")?.Value;
    }
}