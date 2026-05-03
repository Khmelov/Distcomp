using AutoMapper;
using BCrypt.Net;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Repositories;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace Publisher.Services
{
    public class AuthService : IAuthService
    {
        private readonly IRepository<User> _userRepo;
        private readonly ITokenService _tokenService;
        private readonly IConfiguration _config;
        private readonly IMapper _mapper;

        public AuthService(IRepository<User> userRepo, ITokenService tokenService, IConfiguration config, IMapper mapper)
        {
            _userRepo = userRepo;
            _tokenService = tokenService;
            _config = config;
            _mapper = mapper;
        }

        public async Task<UserResponseTo> RegisterAsync(UserRequestTo request)
        {
            // 1. Проверяем, не занят ли логин
            var existingUser = (await _userRepo.FindAsync(u => u.Login == request.Login)).FirstOrDefault();
            if (existingUser != null)
                throw new InvalidOperationException("User with this login already exists");

            // 2. Хэшируем пароль
            var hashedPassword = BCrypt.Net.BCrypt.HashPassword(request.Password);

            // 3. Маппим DTO → сущность, затем переопределяем пароль и роль
            var user = _mapper.Map<User>(request);
            user.Password = hashedPassword;
            user.Role = request.Role?.ToUpper() ?? "CUSTOMER";

            await _userRepo.AddAsync(user);
            await _userRepo.SaveChangesAsync();

            // 4. Маппим обратно в Response
            return _mapper.Map<UserResponseTo>(user);
        }

        public async Task<string> LoginAsync(LoginRequestTo request)
        {
            var user = (await _userRepo.FindAsync(u => u.Login == request.Login)).FirstOrDefault();

            if (user == null || !BCrypt.Net.BCrypt.Verify(request.Password, user.Password))
                throw new UnauthorizedAccessException("Invalid login or password");

            return _tokenService.GenerateToken(user);
        }

        public async Task<ClaimsPrincipal?> VerifyTokenAsync(string token)
        {
            if (string.IsNullOrWhiteSpace(token))
                return null;

            if (token.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
                token = token.Substring("Bearer ".Length).Trim();

            return await _tokenService.ValidateTokenAsync(token);
        }

        public async Task<User?> GetUserFromTokenAsync(string token)
        {
            var principal = await VerifyTokenAsync(token);
            if (principal == null)
                return null;

            var login = principal.FindFirst(ClaimTypes.NameIdentifier)?.Value
                     ?? principal.FindFirst("sub")?.Value;
            if (string.IsNullOrEmpty(login))
                return null;

            var users = await _userRepo.FindAsync(u => u.Login == login);
            return users.FirstOrDefault();
        }

        public async Task<string?> GetRoleFromTokenAsync(string token)
        {
            var principal = await VerifyTokenAsync(token);
            return principal?.FindFirst("role")?.Value;
        }

        public async Task<User> GetCurrentUserAsync(HttpContext httpContext)
        {
            string authHeader = httpContext.Request.Headers["Authorization"].ToString();
            var user = await GetUserFromTokenAsync(authHeader);
            if (user == null)
                throw new UnauthorizedAccessException("Invalid or expired token");
            return user;
        }

        public bool CanRead(ClaimsPrincipal userPrincipal, long? resourceOwnerId = null, long? currentUserId = null)
        {
            var role = userPrincipal.FindFirst("role")?.Value;
            if (string.Equals(role, "ADMIN", StringComparison.OrdinalIgnoreCase)) return true;
            // CUSTOMER может читать всё
            return true;
        }

        public bool CanModify(ClaimsPrincipal userPrincipal, long resourceOwnerId, long currentUserId)
        {
            var role = userPrincipal.FindFirst("role")?.Value;
            if (string.Equals(role, "ADMIN", StringComparison.OrdinalIgnoreCase)) return true;
            if (string.Equals(role, "CUSTOMER", StringComparison.OrdinalIgnoreCase) && resourceOwnerId == currentUserId) return true;
            return false;
        }
    }
}