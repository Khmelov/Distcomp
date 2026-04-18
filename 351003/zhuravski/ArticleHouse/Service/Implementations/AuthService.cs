using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Additions.DAO;
using Additions.Service;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using Microsoft.IdentityModel.Tokens;

namespace ArticleHouse.Service.Implementations;

public class AuthService : BasicService, IAuthService
{
    private readonly ICreatorDAO creatorDao;
    private readonly IConfiguration config;
    private readonly ILogger<AuthService> logger;

    public AuthService(ICreatorDAO creatorDao, IConfiguration config, ILogger<AuthService> logger)
    {
        this.creatorDao = creatorDao;
        this.config = config;
        this.logger = logger;
    }

    public async Task<AuthResponseDTO> LoginAsync(LoginRequestDTO dto)
    {
        CreatorModel creator = default!;
        try
        {
            creator = await creatorDao.GetByLoginAsync(dto.Login); 
        }
        catch (DAOObjectNotFoundException)
        {
            throw new ServiceException("Invalid credentials", 401);
        }
        if (!BCrypt.Net.BCrypt.Verify(dto.Password, creator.Password))
        {
            throw new ServiceException("Invalid credentials", 401);//40101);
        }
        var token = GenerateJwtToken(creator);
        return new AuthResponseDTO { AccessToken = token, TokenType = "Bearer" };
    }

    public async Task<CreatorResponseDTO> RegisterAsync(CreatorRegistrationDTO dto)
    {
        try
        {
            await creatorDao.GetByLoginAsync(dto.Login);
            throw new ServiceException("Login already exists", 409);//40901);
        }
        catch (DAOObjectNotFoundException) {}
        var model = new CreatorModel
        {
            Login = dto.Login,
            Password = BCrypt.Net.BCrypt.HashPassword(dto.Password),
            FirstName = dto.FirstName,
            LastName = dto.LastName,
            Role = dto.Role ?? CreatorModel.CUSTOMER_ROLE
        };

        var created = await InvokeLowerMethod(() => creatorDao.AddNewAsync(model));
        return MakeResponseFromModel(created);
    }

    public async Task<CreatorResponseDTO> GetCurrentCreatorAsync(string login)
    {
        CreatorModel model = await InvokeLowerMethod(() => creatorDao.GetByLoginAsync(login));
        return MakeResponseFromModel(model);
    }

    private string GenerateJwtToken(CreatorModel creator)
    {
        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(config["Jwt:Key"]!));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var claims = new[]
        {
            new Claim(ClaimTypes.Name, creator.Login),
            new Claim(ClaimTypes.Role, creator.Role),
            new Claim(JwtRegisteredClaimNames.Iat, DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString(), ClaimValueTypes.Integer64)
        };

        var token = new JwtSecurityToken(
            config["Jwt:Issuer"],
            config["Jwt:Audience"],
            claims,
            expires: DateTime.UtcNow.AddMinutes(60),
            signingCredentials: creds
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    private static CreatorResponseDTO MakeResponseFromModel(CreatorModel model)
    {
        return new CreatorResponseDTO()
        {
            Id = model.Id,
            FirstName = model.FirstName,
            LastName = model.LastName,
            Login = model.Login,
            Role = model.Role
        };
    }
}